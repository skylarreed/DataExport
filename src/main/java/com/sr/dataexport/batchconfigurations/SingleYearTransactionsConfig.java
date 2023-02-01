package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.listeners.MainChunkListener;
import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.processors.YearProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SingleYearTransactionsConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SynchronizedItemStreamReader<Transaction> allTransactionsReader;
    private final YearProcessor yearProcessor;
    private final SynchronizedItemStreamWriter<Transaction> staxWriter;

    private final ThreadPoolTaskExecutor taskExecutor;

    /**
     * @param jobRepository
     * @param transactionManager
     * @param allTransactionsReader
     * @param yearProcessor
     * @param staxWriter
     * @param taskExecutor
     * @Description This constructor is used to inject the required dependencies.
     */

    public SingleYearTransactionsConfig(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager,
                                        SynchronizedItemStreamReader<Transaction> allTransactionsReader,
                                        YearProcessor yearProcessor,
                                        SynchronizedItemStreamWriter<Transaction> staxWriter,
                                        ThreadPoolTaskExecutor taskExecutor) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.allTransactionsReader = allTransactionsReader;
        this.yearProcessor = yearProcessor;
        this.staxWriter = staxWriter;
        this.taskExecutor = taskExecutor;
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
}
