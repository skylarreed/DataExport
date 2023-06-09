package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.classifiers.StateClassifier;
import com.sr.dataexport.classifiers.UserClassifier;
import com.sr.dataexport.listeners.MainChunkListener;
import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.processors.StateProcessor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class StateTransactionsConfig {
    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;


    private final ThreadPoolTaskExecutor taskExecutor;


    private final SynchronizedItemStreamReader<Transaction> allTransactionsReader;


    private final StateClassifier stateClassifier;




    /**
     * @param jobRepository
     * @param transactionManager
     * @param taskExecutor
     * @param allTransactionsReader
     * @param stateClassifier
     * @Description This constructor is used to inject the required dependencies.
     */


    public StateTransactionsConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ThreadPoolTaskExecutor taskExecutor,
            SynchronizedItemStreamReader<Transaction> allTransactionsReader,
            StateClassifier stateClassifier) {


        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.taskExecutor = taskExecutor;
        this.allTransactionsReader = allTransactionsReader;
        this.stateClassifier = stateClassifier;
    }







    /**
     * @return all states transactions step
     *
     * @Description This method is used to configure the step to export all users transactions.
     */
    @Bean
    public Step exportStateTransactions(){
        return new StepBuilder("exportStateTransactionsStep", jobRepository)
                .<Transaction, Transaction>chunk(60000, transactionManager)
                .reader(allTransactionsReader)
                .writer(classifierWriter(stateClassifier))
                .listener(new MainChunkListener())
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        StepExecutionListener.super.beforeStep(stepExecution);
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        stateClassifier.close();
                        return StepExecutionListener.super.afterStep(stepExecution);
                    }
                })
                .taskExecutor(taskExecutor)
                .build();
    }

    /**
     * @return Job
     *
     * @Description This method is used to configure the job to export all users transactions.
     */
    @Bean(name = "exportStateTransactionsJob")
    public Job exportStateTransactionsJob(){
        return new JobBuilder("exportUserTransactionsJob", jobRepository)
                .start(exportStateTransactions())
                .build();
    }

    /**
     * @return ClassifierCompositeItemWriter
     *
     * @Description This method is used to configure the classifier writer which takes a classifier.
     * The classifierwriter helps to write to different files based on the classifier.
     */
    @Bean("stateWriter")
    public ClassifierCompositeItemWriter<Transaction> classifierWriter(StateClassifier stateClassifier) {
        ClassifierCompositeItemWriter<Transaction> classifier = new ClassifierCompositeItemWriter<>();
        classifier.setClassifier(stateClassifier);
        return classifier;
    }
}
