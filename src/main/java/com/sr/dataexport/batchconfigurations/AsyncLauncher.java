package com.sr.dataexport.batchconfigurations;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class AsyncLauncher {


    private final JobRepository jobRepository;

    public AsyncLauncher(BatchTaskExecutor taskExecutor, JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * @return Job Launcher.
     *
     * @Description This method is used to configure a job launcher that will run asynchronously.
     */

    @Bean(name = "asyncJobLauncher")
    public JobLauncher jobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();

        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}
