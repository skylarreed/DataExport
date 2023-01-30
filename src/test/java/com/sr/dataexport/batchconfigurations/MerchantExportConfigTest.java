package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.classifiers.MerchantClassifier;
import com.sr.dataexport.processors.MerchantProcessor;
import com.sr.dataexport.readers.TransactionReader;
import com.sr.dataexport.writer.Stax;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;

@EnableAutoConfiguration
@SpringBatchTest
@SpringJUnitConfig(classes = {MerchantExportConfig.class, BatchTaskExecutor.class,
        TransactionReader.class, MerchantClassifier.class, Stax.class, MerchantProcessor.class})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j(topic = "MerchantExportConfigTest")
class MerchantExportConfigTest {


    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    JobRepositoryTestUtils jobRepositoryTestUtils;


    @Test
    void testIfJobIsFailedAfterRunningExportAllMerchantsJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/test/resources/input/error_transactions.csv")
                .addString("destination", "src/test/resources/output/merchant-transactions.xml")
                .addString("time", LocalDateTime.now().toString())
                .toJobParameters();
        try{
            jobLauncherTestUtils.launchJob(jobParameters);
        } catch (Exception e) {
            log.info(e.getMessage());
            Assertions.assertTrue(e.getMessage().contains("Job failed to start. Contact the administrator."));
        }

    }
}