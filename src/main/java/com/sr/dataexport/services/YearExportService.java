package com.sr.dataexport.services;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
