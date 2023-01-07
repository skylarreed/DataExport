package com.sr.dataexport.services;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserExportService {

    private final Job singleUserExportJob;

    private final JobLauncher jobLauncher;

    public UserExportService(@Qualifier("singleUserTransactions") Job singleUserExportJob, JobLauncher jobLauncher) {
        this.singleUserExportJob = singleUserExportJob;
        this.jobLauncher = jobLauncher;
    }

    public ResponseEntity<?> exportSingleUserTransactions(String outputPath, long userId){
        try{
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", "src/main/resources/transactions.csv")
                    .addString("outputPath", outputPath + "/" + userId + "_transactions.xml")
                    .addLong("userId", userId)
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(singleUserExportJob, jobParameters);

            if(jobExecution.getStatus() == BatchStatus.COMPLETED){
                return ResponseEntity.ok().build();
            }else{
                return ResponseEntity.badRequest().build();
            }
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
