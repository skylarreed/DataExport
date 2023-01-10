package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.listeners.MainChunkListener;
import com.sr.dataexport.models.Transaction;
import com.sr.dataexport.models.User;
import com.sr.dataexport.processors.ReadUserProcessor;
import com.sr.dataexport.processors.SingleUserTransactionsProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing
public class UserExportConfig {

    private final SynchronizedItemStreamReader<Transaction> singleUserTransactionReader;

    private final SynchronizedItemStreamWriter<Transaction> writer;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final SingleUserTransactionsProcessor singleUserTransactionsProcessor;

    private final SimpleAsyncTaskExecutor taskExecutor;

    private final SynchronizedItemStreamReader<User> allUsersReader;

    private final ReadUserProcessor readUserProcessor;

    private final SynchronizedItemStreamReader<Transaction> allTransactionsReader;

    private final ClassifierCompositeItemWriter<Transaction> classifiedWriter;





    public UserExportConfig(@Qualifier("userTransactionsReader") SynchronizedItemStreamReader<Transaction> reader,
                            SynchronizedItemStreamWriter<Transaction> writer, JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            SingleUserTransactionsProcessor singleUserTransactionsProcessor,
                            SimpleAsyncTaskExecutor taskExecutor, SynchronizedItemStreamReader<User> allUsersReader,
                            ReadUserProcessor readUserProcessor, SynchronizedItemStreamReader<Transaction> allTransactionsReader, ClassifierCompositeItemWriter<Transaction> classifiedWriter) {
        this.singleUserTransactionReader = reader;
        this.writer = writer;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.singleUserTransactionsProcessor = singleUserTransactionsProcessor;
        this.taskExecutor = taskExecutor;

        this.allUsersReader = allUsersReader;
        this.readUserProcessor = readUserProcessor;

        this.allTransactionsReader = allTransactionsReader;
        this.classifiedWriter = classifiedWriter;
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
                .<Transaction, Transaction>chunk(800, transactionManager)
                .reader(singleUserTransactionReader)
                .processor(singleUserTransactionsProcessor)
                .writer(writer)
                .listener(new MainChunkListener())
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean(name = "singleUserTransactions")
    public Job singleUserTransactionsJob(){
        return new JobBuilder("singleUserTransactions", jobRepository)
                .start(userTransactionsStep())
                .build();
    }

    @Bean
    public Step readUserIdStep(){
        return new StepBuilder("readUsers", jobRepository)
                .<User, User>chunk(800, transactionManager)
                .reader(allUsersReader)
                .processor(readUserProcessor)
                .writer(chunk -> System.out.println("chunk = " + chunk))
                .listener(new MainChunkListener())
                .taskExecutor(taskExecutor)
                .build();

    }

    @Bean(name = "readUsers")
    public Job readUsersJob(){
        return new JobBuilder("readUsers", jobRepository)
                .start(readUserIdStep())
                .build();
    }


    @Bean(name = "asyncJobLauncher")
    public JobLauncher jobLauncher() throws Exception {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(8);

        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(taskExecutor);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public Step exportUserTransactions(){
        return new StepBuilder("exportUserTransactionsStep", jobRepository)
                .<Transaction, Transaction>chunk(1000, transactionManager)
                .reader(allTransactionsReader)
                .writer(classifiedWriter)
                .listener(new MainChunkListener())
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean(name = "exportUserTransactionsJob")
    public Job exportUserTransactionsJob(){
        return new JobBuilder("exportUserTransactionsJob", jobRepository)
                .start(exportUserTransactions())
                .build();
    }

}
