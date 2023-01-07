package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.processors.SingleUserTransactionsProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
public class UserExportConfig {

    private final SynchronizedItemStreamReader<Transaction> singleUserTransactionReader;

    private final SynchronizedItemStreamWriter<Transaction> writer;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final SingleUserTransactionsProcessor singleUserTransactionsProcessor;

    private final SimpleAsyncTaskExecutor taskExecutor;

    public UserExportConfig(SynchronizedItemStreamReader<Transaction> reader, SynchronizedItemStreamWriter<Transaction> writer, JobRepository jobRepository, PlatformTransactionManager transactionManager, SingleUserTransactionsProcessor singleUserTransactionsProcessor, SimpleAsyncTaskExecutor taskExecutor) {
        this.singleUserTransactionReader = reader;
        this.writer = writer;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.singleUserTransactionsProcessor = singleUserTransactionsProcessor;
        this.taskExecutor = taskExecutor;
    }

    @Bean
    @StepScope
    public SynchronizedItemStreamWriter<Transaction> userTransactionWriter(@Value("#{jobParameters['outputPath']}") String outputPath) {
        XStreamMarshaller marshaller = new XStreamMarshaller();
        StaxEventItemWriter<Transaction> writer = new StaxEventItemWriter<>();
        writer.setRootTagName("transactions");
        writer.setMarshaller(marshaller);
        writer.setResource(new FileSystemResource(outputPath));

        return new SynchronizedItemStreamWriterBuilder<Transaction>()
                .delegate(writer)
                .build();
    }

    @Bean
    public Step userTransactionsStep(){
        return new StepBuilder("userTransactions", jobRepository)
                .<Transaction, Transaction>chunk(20000, transactionManager)
                .reader(singleUserTransactionReader)
                .processor(singleUserTransactionsProcessor)
                .writer(writer)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean(name = "singleUserTransactions")
    public Job singleUserTransactionsJob(){
        return new JobBuilder("singleUserTransactions", jobRepository)
                .start(userTransactionsStep())
                .build();
    }

}
