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

@Service
public class StateExportService {
    private final JobLauncher jobLauncher;

    private final Job singleStateTransactionsJob;

    private final Job exportStateTransactionsJob;

    public StateExportService(JobLauncher jobLauncher,
                              @Qualifier("singleStateTransactions") Job singleStateTransactionsJob,
                              @Qualifier("exportStateTransactionsJob") Job exportStateTransactionsJob) {
        this.jobLauncher = jobLauncher;
        this.singleStateTransactionsJob = singleStateTransactionsJob;
        this.exportStateTransactionsJob = exportStateTransactionsJob;
    }

    public ResponseEntity<?> launchSingleStateTransactionsJob(String destination, String state) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/main/resources/transactions.csv")
                .addString("destination", destination + "/state-" + state + "-transactions.xml")
                .addString("time", LocalDateTime.now().toString())
                .addString("state", state)
                .toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(singleStateTransactionsJob, jobParameters);
            if (jobExecution.getStatus() == BatchStatus.FAILED) {
                return ResponseEntity.status(500).body("Job failed to start. Contact the administrator.");
            }
            return ResponseEntity.status(202).body("Job started");
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<?> launchExportStateTransactionsJob(String destination) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/main/resources/transactions.csv")
                .addString("destination", destination)
                .addString("time", LocalDateTime.now().toString())
                .toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(exportStateTransactionsJob, jobParameters);
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
