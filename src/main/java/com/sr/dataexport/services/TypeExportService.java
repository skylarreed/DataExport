package com.sr.dataexport.services;

import org.springframework.batch.core.*;
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
            JobExecution jobExecution = jobLauncher.run(transactionTypeJob, jobParameters);
            if (jobExecution.getStatus() == BatchStatus.FAILED) {
                return ResponseEntity.status(500).body("Job failed to start. Contact the administrator.");
            }
            return ResponseEntity.status(202).body("Job started");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<?> launchSingleTransactionTypeJob(String destination, String type) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/main/resources/transactions.csv")
                .addString("destination", destination + "/type-" + type + "-transactions.xml")
                .addString("time", LocalDateTime.now().toString())
                .addString("transactionType", type)
                .toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(singleTransactionTypeJob, jobParameters);
            if (jobExecution.getStatus() == BatchStatus.FAILED) {
                return ResponseEntity.status(500).body("Job failed to start. Contact the administrator.");
            }
            return ResponseEntity.status(202).body("Job started");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
