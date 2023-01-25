package com.sr.dataexport.controllers;

import com.sr.dataexport.services.MerchantExportService;
import com.sr.dataexport.services.UserExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
     * @param destination
     * @param merchantId
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to expose the endpoint to export a single merchant's transactions.
     */
    @GetMapping("/merchants/{merchantId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Export a single merchant's transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted, Job started"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, Job failed to start. Contact the administrator."),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> exportSingleMerchantTransactions(@RequestParam("destination") String destination,
                                                              @PathVariable long merchantId){
        return merchantExportService.exportSingleMerchantTransactions(destination, merchantId);
    }

    /**
     * @param outputPath
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to expose the endpoint to export all merchants' transactions.
     */
    @GetMapping("/merchants")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Export all merchants' transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted, Job started"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, Job failed to start. Contact the administrator."),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> exportAllMerchantsTransactions(@RequestParam("destination") String outputPath){
        return merchantExportService.exportMerchantTransactions(outputPath);
    }

}
