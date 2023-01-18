package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.classifiers.StateClassifier;
import com.sr.dataexport.classifiers.YearClassifier;
import com.sr.dataexport.listeners.MainChunkListener;
import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.processors.YearProcessor;
import org.springframework.batch.core.*;
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
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class YearTransactionsConfig {
    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;


    private final SimpleAsyncTaskExecutor taskExecutor;


    private final SynchronizedItemStreamReader<Transaction> allTransactionsReader;

    private final YearProcessor yearProcessor;

    private final YearClassifier yearClassifier;
    private final SynchronizedItemStreamWriter<Transaction> staxWriter;




    /**
     * @param jobRepository
     * @param transactionManager
     * @param taskExecutor
     * @param allTransactionsReader
     * @param yearProcessor
     * @param yearClassifier
     * @param staxWriter
     * @Description This constructor is used to inject the required dependencies.
     */


    public YearTransactionsConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SimpleAsyncTaskExecutor taskExecutor,
            SynchronizedItemStreamReader<Transaction> allTransactionsReader,
            YearProcessor yearProcessor, YearClassifier yearClassifier, SynchronizedItemStreamWriter<Transaction> staxWriter) {


        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.taskExecutor = taskExecutor;
        this.allTransactionsReader = allTransactionsReader;
        this.yearProcessor = yearProcessor;
        this.yearClassifier = yearClassifier;

        this.staxWriter = staxWriter;
    }

    /**
     * @return Year transactions step.
     *
     * @Description This method is used to configure the step to export a single Years transactions.
     */

    @Bean
    public Step yearTransactionsStep(){
        return new StepBuilder("yearTransactions", jobRepository)
                .<Transaction, Transaction>chunk(60000, transactionManager)
                .reader(allTransactionsReader)
                .processor(yearProcessor)
                .writer(staxWriter)
                .listener(new MainChunkListener())
                .taskExecutor(taskExecutor)
                .build();
    }

    /**
     * @return single Year transaction job.
     *
     * @Description this method is used to configure the job to export a single Year transactions.
     */
    @Bean(name = "singleYearTransactions")
    public Job singleYearTransactionsJob(){
        return new JobBuilder("singleYearTransactions", jobRepository)
                .start(yearTransactionsStep())
                .build();
    }



    /**
     * @return all states transactions step
     *
     * @Description This method is used to configure the step to export all users transactions.
     */
    @Bean
    public Step exportYearTransactions(){
        return new StepBuilder("exportYearTransactionsStep", jobRepository)
                .<Transaction, Transaction>chunk(60000, transactionManager)
                .reader(allTransactionsReader)
                .writer(classifierWriter(yearClassifier))
                .listener(new MainChunkListener())
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        StepExecutionListener.super.beforeStep(stepExecution);
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        yearClassifier.close();
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
    @Bean(name = "exportYearTransactionsJob")
    public Job exportYearTransactionsJob(){
        return new JobBuilder("exportUserTransactionsJob", jobRepository)
                .start(exportYearTransactions())
                .build();
    }

    /**
     * @return ClassifierCompositeItemWriter
     *
     * @Description This method is used to configure the classifier writer which takes a classifier.
     * The classifierwriter helps to write to different files based on the classifier.
     */
    @Bean("yearWriter")
    public ClassifierCompositeItemWriter<Transaction> classifierWriter(YearClassifier yearClassifier) {
        ClassifierCompositeItemWriter<Transaction> classifier = new ClassifierCompositeItemWriter<>();
        classifier.setClassifier(yearClassifier);
        return classifier;
    }
}
