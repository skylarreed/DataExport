package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.classifiers.MerchantClassifier;
import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.processors.MerchantProcessor;
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
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author sr
 * @ClassName MerchantExportConfig
 * @Description This class is used to configure the merchant export job. It contains both the
 * single merchant export step and the step to export all merchants. It also contains the classified writer
 * to write the merchants to their respective files.
 */
@Configuration
public class MerchantExportConfig {
    private final SynchronizedItemStreamReader<Transaction> transactionReader;


    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final ThreadPoolTaskExecutor taskExecutor;

    private final MerchantProcessor merchantProcessor;

    private final MerchantClassifier merchantClassifier;

    private final SynchronizedItemStreamWriter<Transaction> staxWriter;

    /**
     * @param transactionReader
     * @param jobRepository
     * @param transactionManager
     * @param taskExecutor
     * @param merchantProcessor
     * @param merchantClassifier
     * @param staxWriter
     * @Description This constructor is used to inject the required dependencies.
     */
    public MerchantExportConfig(@Qualifier("allTransactionsReader") SynchronizedItemStreamReader<Transaction> transactionReader,
                                JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                ThreadPoolTaskExecutor taskExecutor, MerchantProcessor merchantProcessor,
                                MerchantClassifier merchantClassifier, SynchronizedItemStreamWriter<Transaction> staxWriter) {
        this.transactionReader = transactionReader;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.taskExecutor = taskExecutor;
        this.merchantProcessor = merchantProcessor;
        this.merchantClassifier = merchantClassifier;
        this.staxWriter = staxWriter;
    }

    /**
     * @return The step to export all merchants.
     * @Description This method is used to create the merchant export job.
     */
    public Step merchantExportStep() {
        return new StepBuilder("merchantExportStep", jobRepository)
                .<Transaction, Transaction>chunk(60000, transactionManager)
                .reader(transactionReader)
                .writer(classifierWriter(merchantClassifier))
                .taskExecutor(taskExecutor)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        StepExecutionListener.super.beforeStep(stepExecution);
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        merchantClassifier.close();
                        return StepExecutionListener.super.afterStep(stepExecution);
                    }
                })
                .build();
    }

    /**
     * @return The job to export a single merchant.
     * @Description This method is used to create the merchant export job.
     */
    @Bean
    public Step singleMerchantExportStep(){
        return new StepBuilder("singleMerchantExportStep", jobRepository)
                .<Transaction, Transaction>chunk(60000, transactionManager)
                .reader(transactionReader)
                .processor(merchantProcessor)
                .writer(staxWriter)

                .taskExecutor(taskExecutor)
                .build();
    }

    /**
     * @return the classified writer.
     * @Description This method is used to create the classified writer.
     */
    @Bean("merchantClassifierWriter")
    public ClassifierCompositeItemWriter<Transaction> classifierWriter(MerchantClassifier merchantClassifier) {
        ClassifierCompositeItemWriter<Transaction> classifier = new ClassifierCompositeItemWriter<>();
        classifier.setClassifier(merchantClassifier);
        return classifier;
    }

    /**
     * @return the merchant export job.
     * @Description This method is used to create the merchant export job which exports all transactions for merchants.
     */
    @Bean("merchantExportJob")
    @Primary
    public Job merchantExportJob() {
        return new JobBuilder("merchantExportJob", jobRepository)
                .start(merchantExportStep())
                .build();
    }





}
