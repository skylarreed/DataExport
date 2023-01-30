package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.classifiers.UserClassifier;
import com.sr.dataexport.listeners.MainChunkListener;
import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.processors.UserProcessor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author sr
 * @ClassName UserExportConfig
 * @Description This class is used to configure the user export job. It contains both the single
 * user export step and the step to export all users.
 */
@Configuration
@EnableBatchProcessing
public class UserExportConfig {




    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;


    private final ThreadPoolTaskExecutor taskExecutor;


    private final SynchronizedItemStreamReader<Transaction> allTransactionsReader;


    private final UserProcessor userProcessor;

    private final UserClassifier userClassifier;

    private final SynchronizedItemStreamWriter<Transaction> staxWriter;




    /**
     * @param jobRepository
     * @param transactionManager
     * @param taskExecutor
     * @param allTransactionsReader
     * @param userProcessor
     * @param userClassifier
     * @param staxWriter
     * @Description This constructor is used to inject the required dependencies.
     */


    public UserExportConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ThreadPoolTaskExecutor taskExecutor,
            SynchronizedItemStreamReader<Transaction> allTransactionsReader,
            UserProcessor userProcessor, UserClassifier userClassifier, SynchronizedItemStreamWriter<Transaction> staxWriter) {


        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.taskExecutor = taskExecutor;

        this.allTransactionsReader = allTransactionsReader;


        this.userProcessor = userProcessor;
        this.userClassifier = userClassifier;
        this.staxWriter = staxWriter;
    }

    /**
     * @return user transactions step.
     *
     * @Description This method is used to configure the step to export a single users transactions.
     */

    @Bean
    public Step userTransactionsStep(){
        return new StepBuilder("userTransactions", jobRepository)
                .<Transaction, Transaction>chunk(60000, transactionManager)
                .reader(allTransactionsReader)
                .processor(userProcessor)
                .writer(staxWriter)
                .listener(new MainChunkListener())
                .taskExecutor(taskExecutor)
                .build();
    }

    /**
     * @return single user transaction job.
     *
     * @Description this method is used to configure the job to export a single user transactions.
     */
    @Bean(name = "singleUserTransactions")
    public Job singleUserTransactionsJob(){
        return new JobBuilder("singleUserTransactions", jobRepository)
                .start(userTransactionsStep())
                .build();
    }




    /**
     * @return all users transactions step
     *
     * @Description This method is used to configure the step to export all users transactions.
     */
    @Bean
    public Step exportUserTransactions(){
        return new StepBuilder("exportUserTransactionsStep", jobRepository)
                .<Transaction, Transaction>chunk(10000, transactionManager)
                .reader(allTransactionsReader)
                .writer(classifierWriter(userClassifier))
                .listener(new MainChunkListener())
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        StepExecutionListener.super.beforeStep(stepExecution);
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        userClassifier.close();
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
    @Bean(name = "exportUserTransactionsJob")
    public Job exportUserTransactionsJob(){
        return new JobBuilder("exportUserTransactionsJob", jobRepository)
                .start(exportUserTransactions())
                .build();
    }

    /**
     * @param userClassifier
     * @return ClassifierCompositeItemWriter
     *
     * @Description This method is used to configure the classifier writer which takes a classifier.
     */
    @Bean("userWriter")
    public ClassifierCompositeItemWriter<Transaction> classifierWriter(UserClassifier userClassifier) {
        ClassifierCompositeItemWriter<Transaction> classifier = new ClassifierCompositeItemWriter<>();
        classifier.setClassifier(userClassifier);
        return classifier;
    }

}
