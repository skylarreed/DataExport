package com.sr.dataexport.services;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author sr
 *
 * @Description service for exporting transactions by year.
 */
@Service
public class YearExportService {

    private final Job singleYearExportJob;

    private final Job allYearsExportJob;

    private final JobLauncher jobLauncher;

    public YearExportService(@Qualifier("singleYearTransactions") Job singleYearExportJob,
                             @Qualifier("exportYearTransactionsJob") Job allYearsExportJob, JobLauncher jobLauncher) {
        this.singleYearExportJob = singleYearExportJob;
        this.allYearsExportJob = allYearsExportJob;
        this.jobLauncher = jobLauncher;
    }

    /**
     * @param destination
     * @param year
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to export a single year's transactions.
     */
    public ResponseEntity<?> launchSingleYearExportJob(String destination, String year) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/main/resources/transactions.csv")
                .addString("destination", destination + "/year-" + year + "-transactions.xml")
                .addString("time", LocalDateTime.now().toString())
                .addString("year", year)
                .toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(singleYearExportJob, jobParameters);
            if (jobExecution.getStatus() == BatchStatus.FAILED) {
                return ResponseEntity.status(500).body("Job failed to start. Contact the administrator.");
            }
            return ResponseEntity.status(202).body("Job started");
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * @param destination
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to export all years' transactions.
     */
    public ResponseEntity<?> launchAllYearsExportJob(String destination) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/main/resources/transactions.csv")
                .addString("destination", destination)
                .addString("time", LocalDateTime.now().toString())
                .toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(allYearsExportJob, jobParameters);
            if (jobExecution.getStatus() == BatchStatus.FAILED) {
                return ResponseEntity.status(500).body("Job failed to start. Contact the administrator.");
            }
            return ResponseEntity.status(202).body("Job started");
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
