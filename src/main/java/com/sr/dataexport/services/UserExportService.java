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
 * @ClassName MerchantExportService
 * @Description This class is used as a service layer to handle the business logic for exporting user transactions.
 *
 */
@Service
public class UserExportService {

    private final Job singleUserExportJob;


    private final Job exportUserTransactionsJob;

    private final JobLauncher jobLauncher;



    public UserExportService(@Qualifier("singleUserTransactions") Job singleUserExportJob,
                             @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
                             @Qualifier("exportUserTransactionsJob") Job exportUserTransactionsJob) {
        this.singleUserExportJob = singleUserExportJob;
        this.jobLauncher = jobLauncher;

        this.exportUserTransactionsJob = exportUserTransactionsJob;
    }

    /**
     * @param destination
     * @param userId
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to export a single user's transactions.
     */
    public ResponseEntity<?> exportSingleUserTransactions(String destination, long userId) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", "src/main/resources/transactions.csv")
                    .addString("destination", destination + "/user-" + userId + "-transactions.xml")
                    .addLong("userId", userId)
                    .addString("time", LocalDateTime.now().toString())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(singleUserExportJob, jobParameters);

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
     * @Description This method is used to export all users' transactions.
     */
    public ResponseEntity<?> exportAllUsersTransactions(String destination) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", "src/main/resources/transactions.csv")
                    .addString("destination", destination)
                    .addString("time", LocalDateTime.now().toString())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(exportUserTransactionsJob, jobParameters);
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
