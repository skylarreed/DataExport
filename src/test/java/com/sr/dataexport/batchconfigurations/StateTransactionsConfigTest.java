package com.sr.dataexport.batchconfigurations;

import com.sr.dataexport.classifiers.MerchantClassifier;
import com.sr.dataexport.classifiers.StateClassifier;
import com.sr.dataexport.processors.MerchantProcessor;
import com.sr.dataexport.processors.StateProcessor;
import com.sr.dataexport.readers.TransactionReader;
import com.sr.dataexport.writer.Stax;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@SpringBatchTest
@SpringJUnitConfig(classes = {StateTransactionsConfig.class, BatchTaskExecutor.class,
        TransactionReader.class, StateClassifier.class, Stax.class})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j(topic = "StateTransactionsConfigTest")
class StateTransactionsConfigTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    JobRepositoryTestUtils jobRepositoryTestUtils;


    @AfterEach
    void deleteOutputDirectory() {
        File outputDirectory = new File("src/test/resources/output");
        if (outputDirectory.exists()) {
            File[] files = outputDirectory.listFiles();
            for (File file : files) {
                file.delete();
            }
            outputDirectory.delete();
        }
    }
    @Test
    void testIfJobIsFailedAfterRunningAllStatesJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/test/resources/input/error_transactions.csv")
                .addString("destination", "src/test/resources/output")
                .addString("time", LocalDateTime.now().toString())
                .toJobParameters();
        try{
            JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
            List<Throwable> exceptions = jobExecution.getAllFailureExceptions();
            for (Throwable exception : exceptions) {
                System.out.println(exception.getMessage());
                log.info(exception.getMessage());
                log.error(Arrays.toString(exception.getStackTrace()));
                log.error(exception.getCause().getMessage());
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            log.error(e.getCause().getMessage());;
            assertTrue(e.getMessage().contains("Job failed to start. Contact the administrator."));
        }

    }

    @Test
    void testIfJobDoesNotHaveValidPath() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/test/resources/input/this_file_does_not_exist.csv")
                .addString("destination", "src/test/resources/output")
                .addString("time", LocalDateTime.now().toString())
                .toJobParameters();
        JobExecution jobExecution;
        try{
            jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
            assertTrue(jobExecution.getStatus().isUnsuccessful());
            File file = new File("src/test/resources/output/this_file_does_not_exist.csv");
            assertFalse(file.exists());
        } catch (Exception e) {
            log.info(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            log.error(e.getCause().getMessage());;
            Assertions.fail();
        }

    }

    @Test
    void testJobCompletesSuccessfullyWithValidInputAndOutput() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/test/resources/input/reg_transactions.csv")
                .addString("destination", "src/test/resources/output")
                .addString("time", LocalDateTime.now().toString())
                .toJobParameters();
        JobExecution jobExecution;
        try {
            jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
            assertEquals("COMPLETED", jobExecution.getStatus().toString());
        } catch (Exception e) {
            log.info(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            log.error(e.getCause().getMessage());
            Assertions.fail();
        }
    }
}