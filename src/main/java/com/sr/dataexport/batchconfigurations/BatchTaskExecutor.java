package com.sr.dataexport.batchconfigurations;

import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @ClassName BatchTaskExecutor
 * @Description This class is used to configure the task executor for the batch jobs.
 */
@Component
public class BatchTaskExecutor{

    /**
     * @return SimpleAsyncTaskExecutor
     * @Description This method is used to configure the task executor for the batch jobs.
     */
    @Bean
    public SimpleAsyncTaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setThreadNamePrefix("spring-batch-");
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }
}
