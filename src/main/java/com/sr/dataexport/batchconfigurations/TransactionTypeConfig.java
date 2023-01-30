package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.classifiers.TransactionTypeClassifier;
import com.sr.dataexport.listeners.MainChunkListener;
import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.processors.TransactionTypeProcessor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @ClassName TransactionTypeConfig
 * @Description This class is used to configure the job for transaction type.
 */
@Configuration
public class TransactionTypeConfig {

    private final TransactionTypeClassifier transactionTypeClassifier;

    private final SynchronizedItemStreamReader<Transaction> transactionReader;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final SynchronizedItemStreamWriter<Transaction> staxWriter;

    private final TransactionTypeProcessor transactionTypeProcessor;
    private final ThreadPoolTaskExecutor taskExecutor;

    /**
     * @param transactionTypeClassifier
     * @param transactionReader
     * @param jobRepository
     * @param transactionManager
     * @param staxWriter
     * @param transactionTypeProcessor
     * @param taskExecutor
     * @Description This constructor is used to inject the required dependencies.
     */
    public TransactionTypeConfig(TransactionTypeClassifier transactionTypeClassifier,
                                 @Qualifier("allTransactionsReader") SynchronizedItemStreamReader<Transaction> transactionReader,
                                 JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                 SynchronizedItemStreamWriter<Transaction> staxWriter, TransactionTypeProcessor transactionTypeProcessor, ThreadPoolTaskExecutor taskExecutor) {
        this.transactionTypeClassifier = transactionTypeClassifier;
        this.transactionReader = transactionReader;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.staxWriter = staxWriter;
        this.transactionTypeProcessor = transactionTypeProcessor;
        this.taskExecutor = taskExecutor;
    }

    /**
     * @return ClassifierCompositeItemWriter<Transaction>
     * @Description This method is used create the classified writer for the transaction type
     */
    @Bean
    public ClassifierCompositeItemWriter<Transaction> transactionTypeClassifiedWriter() {
        ClassifierCompositeItemWriter<Transaction> classifier = new ClassifierCompositeItemWriter<>();
        classifier.setClassifier(transactionTypeClassifier);
        return classifier;
    }

    /**
     * @return Step
     * @Description This method is used to create the step for transaction type.
     */
    @Bean
    public Step transactionTypeStep() {
        return new StepBuilder("transactionTypeStep", jobRepository)
                .<Transaction, Transaction>chunk(60000, transactionManager)
                .reader(transactionReader)
                .writer(transactionTypeClassifiedWriter())
                .listener(new MainChunkListener())
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        StepExecutionListener.super.beforeStep(stepExecution);
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        transactionTypeClassifier.close();
                        return StepExecutionListener.super.afterStep(stepExecution);
                    }
                })
                .taskExecutor(taskExecutor)
                .build();
    }

    /**
     * @return Job
     * @Description This method is used to create the job for the export of all transaction types.
     */
    @Bean(name = "transactionTypeJob")
    public Job transactionTypeJob() {
        return new JobBuilder("transactionTypeJob", jobRepository)
                .start(transactionTypeStep())
                .build();
    }

    /**
     * @return Step
     * @Description This method is used to create the step for a single transaction type.
     */
    @Bean
    public Step singleTransactionType(){
        return new StepBuilder("singleTransactionType", jobRepository)
                .<Transaction, Transaction>chunk(60000, transactionManager)
                .reader(transactionReader)
                .processor(transactionTypeProcessor)
                .writer(staxWriter)
                .listener(new MainChunkListener())
                .taskExecutor(taskExecutor)
                .build();
    }

    /**
     * @return Job
     * @Description This method is used to create the job for a single transaction type.
     */
    @Bean(name = "singleTransactionTypeJob")
    public Job singleTransactionTypeJob(){
        return new JobBuilder("singleTransactionTypeJob", jobRepository)
                .start(singleTransactionType())
                .build();
    }
}
