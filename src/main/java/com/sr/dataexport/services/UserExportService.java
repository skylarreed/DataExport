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
public class UserExportService {

    private final Job singleUserExportJob;

    private final Job readUserJob;


    private final Job exportUserTransactionsJob;

    private final JobLauncher jobLauncher;



    public UserExportService(@Qualifier("singleUserTransactions") Job singleUserExportJob,
                             @Qualifier("readUsers") Job readUserJob,
                             @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
                             @Qualifier("exportUserTransactionsJob") Job exportUserTransactionsJob) {
        this.singleUserExportJob = singleUserExportJob;
        this.readUserJob = readUserJob;
        this.jobLauncher = jobLauncher;

        this.exportUserTransactionsJob = exportUserTransactionsJob;
    }

    public ResponseEntity<?> exportSingleUserTransactions(String outputPath, long userId) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", "src/main/resources/transactions.csv")
                    .addString("outputPath", outputPath + "/" + userId + "_transactions.xml")
                    .addString("time", LocalDateTime.now().toString())
                    .addLong("userId", userId)
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(singleUserExportJob, jobParameters);

            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<?> exportAllUsersTransactions(String outputPath) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", "src/main/resources/transactions.csv")
                    .addString("time", LocalDateTime.now().toString())
                    .addString("destination", outputPath)
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(exportUserTransactionsJob, jobParameters);

            return ResponseEntity.ok().build();

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }








}
