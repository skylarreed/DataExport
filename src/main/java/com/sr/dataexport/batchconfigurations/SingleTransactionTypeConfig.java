package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.listeners.MainChunkListener;
import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.processors.TransactionTypeProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SingleTransactionTypeConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SynchronizedItemStreamReader<Transaction> transactionReader;
    private final TransactionTypeProcessor transactionTypeProcessor;

    private final SynchronizedItemStreamWriter<Transaction> staxWriter;

    private final ThreadPoolTaskExecutor taskExecutor;

    public SingleTransactionTypeConfig(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       @Qualifier("allTransactionsReader") SynchronizedItemStreamReader<Transaction> transactionReader,
                                       TransactionTypeProcessor transactionTypeProcessor,
                                       SynchronizedItemStreamWriter<Transaction> staxWriter,
                                       ThreadPoolTaskExecutor taskExecutor) {

        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.transactionReader = transactionReader;
        this.transactionTypeProcessor = transactionTypeProcessor;
        this.staxWriter = staxWriter;
        this.taskExecutor = taskExecutor;
    }

    /**
     * @param jobRepository
     * @param transactionManager
     * @param transactionReader
     * @param transactionTypeProcessor
     * @param staxWriter
     * @param taskExecutor
     * @Description This constructor is used to inject the required dependencies.
     */

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
