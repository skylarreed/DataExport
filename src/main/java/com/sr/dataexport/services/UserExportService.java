package com.sr.dataexport.services;

import com.sr.dataexport.utils.UserTracker;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserExportService {

    private final Job singleUserExportJob;

    private final Job readUserJob;

    private final JobLauncher jobLauncher;

    public UserExportService(@Qualifier("singleUserTransactions") Job singleUserExportJob,
                             @Qualifier("readUsers") Job readUserJob, @Qualifier("asyncJobLauncher") JobLauncher jobLauncher) {
        this.singleUserExportJob = singleUserExportJob;
        this.readUserJob = readUserJob;
        this.jobLauncher = jobLauncher;
    }

    public ResponseEntity<?> exportSingleUserTransactions(String outputPath, long userId) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", "src/main/resources/transactions.csv")
                    .addString("outputPath", outputPath + "/" + userId + "_transactions.xml")
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
                    .addString("outputPath", outputPath)
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(readUserJob, jobParameters);

            HashSet<Long> users = UserTracker.users;

            for (Long userId : users) {
                JobParameters jobParameters2 = new JobParametersBuilder()
                        .addString("filePath", "src/main/resources/transactions.csv")
                        .addString("outputPath", outputPath + "/" + userId + "_transactions.xml")
                        .addLong("userId", userId)
                        .toJobParameters();
                jobLauncher.run(singleUserExportJob, jobParameters2);
            }
            return ResponseEntity.ok().build();

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
