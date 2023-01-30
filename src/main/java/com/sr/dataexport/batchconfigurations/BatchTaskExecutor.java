package com.sr.dataexport.batchconfigurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    public ThreadPoolTaskExecutor taskExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        log.info("Configuring task executor with core pool size: {}", corePoolSize);

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(corePoolSize);
        threadPoolTaskExecutor.setQueueCapacity(50);
        threadPoolTaskExecutor.setThreadNamePrefix("spring-batch-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
