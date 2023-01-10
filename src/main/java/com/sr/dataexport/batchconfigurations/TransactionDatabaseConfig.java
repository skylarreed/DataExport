package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.listeners.MainChunkListener;
import com.sr.dataexport.models.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class TransactionDatabaseConfig {

    private final DataSource dataSource;

    private final SimpleAsyncTaskExecutor taskExecutor;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final SynchronizedItemStreamReader<Transaction> transactionReader;

    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;





    public TransactionDatabaseConfig(DataSource dataSource, SimpleAsyncTaskExecutor taskExecutor,
                                     JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
                                     @Qualifier("allTransactionsReader") SynchronizedItemStreamReader<Transaction> singleUserTransactionReader, LocalContainerEntityManagerFactoryBean entityManagerFactory) {

        this.dataSource = dataSource;
        this.taskExecutor = taskExecutor;
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.transactionReader = singleUserTransactionReader;

        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public JpaItemWriter<Transaction> transactionWriter() {
        return new JpaItemWriterBuilder<Transaction>()
                .entityManagerFactory(entityManagerFactory.getObject())
                .build();
    }


    @Bean
    public Step transactionStep() {
        return new StepBuilder("transactionStep", jobRepository)
                .<Transaction, Transaction>chunk(800, platformTransactionManager)
                .reader(transactionReader)
                .writer(transactionWriter())
                .listener(new MainChunkListener())
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean(name = "transactionJob")
    public Job dbTransactionJob(){
        return new JobBuilder("dbTransactionJob", jobRepository)
                .start(transactionStep())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Transaction> pagingItemReader(@Value("#{jobParameters['userId']}") long userId) throws Exception {
        Integer id = Math.toIntExact(userId);
        return new JpaPagingItemReaderBuilder<Transaction>()
                .entityManagerFactory(entityManagerFactory.getObject())
                .queryProvider(createQueryProvider(userId))
                .saveState(false)
                .name("transactionDbReader")
                .build();
    }

    private JpaNativeQueryProvider<Transaction> createQueryProvider(long userId) throws Exception {
        String query = "SELECT DISTINCT * FROM transactions WHERE user_id = " + userId;
        JpaNativeQueryProvider<Transaction> queryProvider = new JpaNativeQueryProvider<>();
        queryProvider.setSqlQuery(query);
        queryProvider.setEntityClass(Transaction.class);
        queryProvider.afterPropertiesSet();

        return queryProvider;
    }


}
