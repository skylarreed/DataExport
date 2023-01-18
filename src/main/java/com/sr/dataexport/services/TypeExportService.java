package com.sr.dataexport.services;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TypeExportService {
    private final Job transactionTypeJob;

    private final Job singleTransactionTypeJob;

    private final JobLauncher jobLauncher;

    public TypeExportService(Job transactionTypeJob, Job singleTransactionTypeJob, JobLauncher jobLauncher) {
        this.transactionTypeJob = transactionTypeJob;
        this.singleTransactionTypeJob = singleTransactionTypeJob;
        this.jobLauncher = jobLauncher;
    }

    public ResponseEntity<?> launchTransactionTypeJob(String destination) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/main/resources/transactions.csv")
                .addString("destination", destination)
                .addString("time", LocalDateTime.now().toString())
                .toJobParameters();

        try {
            jobLauncher.run(transactionTypeJob, jobParameters);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<?> launchSingleTransactionTypeJob(String destination, String type) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/main/resources/transactions.csv")
                .addString("destination", destination + "/" + type + "-transactions.xml")
                .addString("time", LocalDateTime.now().toString())
                .addString("type", type)
                .toJobParameters();

        try {
            jobLauncher.run(singleTransactionTypeJob, jobParameters);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
