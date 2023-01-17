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
                .addString("destination", destination + "/" + state + "-transactions.xml")
                .addString("time", LocalDateTime.now().toString())
                .addString("state", state)
                .toJobParameters();

        try {
            jobLauncher.run(singleStateTransactionsJob, jobParameters);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
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
            jobLauncher.run(exportStateTransactionsJob, jobParameters);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
