package com.sr.dataexport.services;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
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
                .addString("destination", destination + "/" + merchantId + "-transactions.xml")
                .addString("time", LocalDateTime.now().toString())
                .addLong("merchantId", merchantId)
                .toJobParameters();

        try {
            jobLauncher.run(singleMerchantExportJob, jobParameters);
            return ResponseEntity.ok().build();
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
            jobLauncher.run(allMerchantExportJob, jobParameters);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

