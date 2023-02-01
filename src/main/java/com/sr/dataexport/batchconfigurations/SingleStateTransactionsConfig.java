package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.listeners.MainChunkListener;
import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.processors.StateProcessor;
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
public class SingleStateTransactionsConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final SynchronizedItemStreamReader<Transaction> allTransactionsReader;

    private final StateProcessor stateProcessor;

    private final SynchronizedItemStreamWriter<Transaction> staxWriter;

    private final ThreadPoolTaskExecutor taskExecutor;

    /**
     * @param jobRepository
     * @param transactionManager
     * @param allTransactionsReader
     * @param stateProcessor
     * @param staxWriter
     * @param taskExecutor
     * @Description This constructor is used to inject the required dependencies.
     */

    public SingleStateTransactionsConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                         @Qualifier("allTransactionsReader") SynchronizedItemStreamReader<Transaction> allTransactionsReader,
                                         StateProcessor stateProcessor,
                                         SynchronizedItemStreamWriter<Transaction> staxWriter, ThreadPoolTaskExecutor taskExecutor) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.allTransactionsReader = allTransactionsReader;
        this.stateProcessor = stateProcessor;
        this.staxWriter = staxWriter;
        this.taskExecutor = taskExecutor;
    }



    /**
     * @return user transactions step.
     *
     * @Description This method is used to configure the step to export a single users transactions.
     */

    @Bean
    public Step stateTransactionsStep(){
        return new StepBuilder("stateTransactions", jobRepository)
                .<Transaction, Transaction>chunk(60000, transactionManager)
                .reader(allTransactionsReader)
                .processor(stateProcessor)
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
    @Bean(name = "singleStateTransactions")
    public Job singleStateTransactionsJob(){
        return new JobBuilder("singleStateTransactions", jobRepository)
                .start(stateTransactionsStep())
                .build();
    }
}
