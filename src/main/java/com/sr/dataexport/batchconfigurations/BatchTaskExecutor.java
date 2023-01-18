package com.sr.dataexport.batchconfigurations;

import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class BatchTaskExecutor{
    @Bean
    public SimpleAsyncTaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setThreadNamePrefix("spring-batch-");
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }
}
