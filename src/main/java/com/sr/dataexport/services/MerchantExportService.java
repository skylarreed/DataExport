package com.sr.dataexport.services;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author sr
 * @ClassName MerchantExportService
 * @Description This class is used as a service layer to handle the business logic for exporting merchant transactions.
 *
 */
@Service
public class MerchantExportService {

    private final Job singleMerchantExportJob;

    private final Job allMerchantExportJob;


    private final JobLauncher jobLauncher;
    public MerchantExportService(@Qualifier("singleMerchantExportJob") Job singleMerchantExportJob,
                                 @Qualifier("merchantExportJob") Job allMerchantExportJob,
                                 @Qualifier("asyncJobLauncher") JobLauncher jobLauncher) {
        this.singleMerchantExportJob = singleMerchantExportJob;
        this.allMerchantExportJob = allMerchantExportJob;
        this.jobLauncher = jobLauncher;
    }

    /**
     * @param destination
     * @param merchantId
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to export a single merchant's transactions.
     */
    public ResponseEntity<?> exportSingleMerchantTransactions(String destination, long merchantId) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/main/resources/transactions.csv")
                .addString("destination", destination + "/merchant-" + merchantId + "-transactions.xml")
                .addString("time", LocalDateTime.now().toString())
                .addLong("merchantId", merchantId)
                .toJobParameters();

        try {
            JobExecution jobExecution =  jobLauncher.run(singleMerchantExportJob, jobParameters);
            if (jobExecution.getStatus() == BatchStatus.FAILED) {
                return ResponseEntity.status(500).body("Job failed to start. Contact the administrator.");
            }
            return ResponseEntity.status(202).body("Job started");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * @param destination
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to export all merchants' transactions.
     */
    public ResponseEntity<?> exportMerchantTransactions(String destination){
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", "src/main/resources/transactions.csv")
                .addString("destination", destination)
                .addString("time", LocalDateTime.now().toString())
                .toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(allMerchantExportJob, jobParameters);
            if (jobExecution.getStatus() == BatchStatus.FAILED) {
                return ResponseEntity.status(500).body("Job failed to start. Contact the administrator.");
            }
            return ResponseEntity.status(202).body("Job started");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

