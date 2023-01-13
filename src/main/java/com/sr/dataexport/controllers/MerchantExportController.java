package com.sr.dataexport.controllers;

import com.sr.dataexport.services.MerchantExportService;
import com.sr.dataexport.services.UserExportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sr
 * @ClassName MerchantExportController
 * @Description This class is used to expose the merchant export endpoints.
 */
@RestController
public class MerchantExportController {

    private final MerchantExportService merchantExportService;

    public MerchantExportController(UserExportService userExportService, MerchantExportService merchantExportService) {
        this.merchantExportService = merchantExportService;
    }

    /**
     * @param outputPath
     * @param merchantId
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to expose the endpoint to export a single merchant's transactions.
     */
    @GetMapping("/merchants/{merchantId}")
    public ResponseEntity<?> exportSingleMerchantTransactions(@RequestParam("destination") String outputPath, @PathVariable long merchantId){
        return merchantExportService.exportSingleMerchantTransactions(outputPath, merchantId);
    }

    /**
     * @param outputPath
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to expose the endpoint to export all merchants' transactions.
     */
    @GetMapping("/merchants")
    public ResponseEntity<?> exportAllMerchantsTransactions(@RequestParam("destination") String outputPath){
        return merchantExportService.exportMerchantTransactions(outputPath);
    }
}
