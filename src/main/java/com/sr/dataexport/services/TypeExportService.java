package com.sr.dataexport.services;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author sr
 * Service for exporting transactions by type.
 */
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

    /**
     * Launches the job to export all transactions by type.
     * @param destination The destination folder for the exported files.
     * @return A response entity with the status of the job.
     */
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
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Launches the job to export transactions by type.
     * @param destination The destination folder for the exported files.
     * @param type The type of transactions to export.
     * @return A response entity with the status of the job.
     */
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
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
