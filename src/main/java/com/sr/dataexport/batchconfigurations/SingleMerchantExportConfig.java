package com.sr.dataexport.batchconfigurations;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SingleMerchantExportConfig {
    private final Step singleMerchantExportStep;

    private final JobRepository jobRepository;
    public SingleMerchantExportConfig(Step singleMerchantExportStep, JobRepository jobRepository) {
        this.singleMerchantExportStep = singleMerchantExportStep;
        this.jobRepository = jobRepository;
    }

    /**
     * @return the single merchant export job.
     * @Description This method is used to create the single merchant export job which exports all transactions for a
     * single merchant.
     */
    @Bean("singleMerchantExportJob")
    public Job singlMerchantExportJob(){
        return new JobBuilder("singleMerchantExportJob", jobRepository)
                .start(singleMerchantExportStep)
                .build();
    }
}
