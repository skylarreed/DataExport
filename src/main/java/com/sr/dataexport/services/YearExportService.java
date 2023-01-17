package com.sr.dataexport.services;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
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
                .addString("destination", destination + "/" + year + "-transactions.xml")
                .addString("time", LocalDateTime.now().toString())
                .addString("year", year)
                .toJobParameters();

        try {
            jobLauncher.run(singleYearExportJob, jobParameters);
            return ResponseEntity.ok().build();
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
            jobLauncher.run(allYearsExportJob, jobParameters);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
