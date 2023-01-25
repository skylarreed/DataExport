package com.sr.dataexport.batchconfigurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @ClassName BatchTaskExecutor
 * @Description This class is used to configure the task executor for the batch jobs.
 */
@Component
@Slf4j
public class BatchTaskExecutor{

    /**
     * @return SimpleAsyncTaskExecutor
     * @Description This method is used to configure the task executor for the batch jobs.
     */
    @Bean
    public SimpleAsyncTaskExecutor taskExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        log.info("Configuring task executor with core pool size: {}", corePoolSize);
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setThreadNamePrefix("spring-batch-");
        taskExecutor.setConcurrencyLimit(corePoolSize);
        return taskExecutor;
    }
}
