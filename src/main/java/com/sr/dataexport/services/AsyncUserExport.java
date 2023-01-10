package com.sr.dataexport.services;

import com.sr.dataexport.utils.UserTracker;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Service
@EnableAsync
public class AsyncUserExport {

    @Async
    public void exportAllUsersTransactionsAsync(String outputPath, JobExecution jobExecution, Job singleUserExportJob, JobLauncher jobLauncher) {
        try {

            while(jobExecution.getStatus() != BatchStatus.COMPLETED){
                Thread.sleep(1000);
            }

            HashMap<Long, String> users = UserTracker.users;

            while(users.size() > 0){
                long userId = users.keySet().iterator().next();
                users.remove(users.keySet().iterator().next());
                JobParameters userJobParams = new JobParametersBuilder()
                        .addString("outputPath", outputPath + "/" + userId + "_transactions.xml")
                        .addLong("userId", userId)
                        .addString("time", LocalDateTime.now().toString())
                        .toJobParameters();

                jobLauncher.run(singleUserExportJob, userJobParams);
            }

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
