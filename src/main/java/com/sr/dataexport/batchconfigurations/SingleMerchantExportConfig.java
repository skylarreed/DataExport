package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.processors.MerchantProcessor;
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
public class SingleMerchantExportConfig {

    private final JobRepository jobRepository;

    private final SynchronizedItemStreamReader<Transaction> transactionReader;

    private final SynchronizedItemStreamWriter<Transaction> staxWriter;

    private final MerchantProcessor merchantProcessor;

    private final ThreadPoolTaskExecutor taskExecutor;

    private final PlatformTransactionManager transactionManager;
    public SingleMerchantExportConfig(JobRepository jobRepository,
                                      @Qualifier("allTransactionsReader") SynchronizedItemStreamReader<Transaction> transactionReader,
                                      SynchronizedItemStreamWriter<Transaction> staxWriter, MerchantProcessor merchantProcessor, ThreadPoolTaskExecutor taskExecutor, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionReader = transactionReader;
        this.staxWriter = staxWriter;
        this.merchantProcessor = merchantProcessor;
        this.taskExecutor = taskExecutor;
        this.transactionManager = transactionManager;
    }

    /**
     * @return the single merchant export job.
     * @Description This method is used to create the single merchant export job which exports all transactions for a
     * single merchant.
     */

    /**
     * @return The job to export a single merchant.
     * @Description This method is used to create the merchant export job.
     */
    @Bean("singleMerchantExportStep")
    public Step singleMerchantExportStep(){
        return new StepBuilder("singleMerchantExportStep", jobRepository)
                .<Transaction, Transaction>chunk(60000, transactionManager)
                .reader(transactionReader)
                .processor(merchantProcessor)
                .writer(staxWriter)
                .taskExecutor(taskExecutor)
                .build();
    }
    @Bean("singleMerchantExportJob")
    public Job singlMerchantExportJob(){
        return new JobBuilder("singleMerchantExportJob", jobRepository)
                .start(singleMerchantExportStep())
                .build();
    }
}
