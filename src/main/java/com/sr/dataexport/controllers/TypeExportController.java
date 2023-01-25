package com.sr.dataexport.controllers;

import com.sr.dataexport.services.TypeExportService;
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
 * @ClassName TypeExportController
 * @Description This class is used to export the data based on the transaction type.
 */
@RestController
public class TypeExportController {

    private final TypeExportService typeExportService;

    public TypeExportController(TypeExportService typeExportService) {
        this.typeExportService = typeExportService;
    }

    /**
     * @param destination
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to call the service to export all transaction types' transactions.
     *
     */
    @GetMapping("/transactionTypes")
    @Operation(summary = "Export all transaction types")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted, Job started"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, Job failed to start. Contact the administrator."),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> exportAllType(@RequestParam String destination) {
        return typeExportService.launchTransactionTypeJob(destination);
    }

    /**
     * @param type
     * @param destination
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to call the service to export a single transaction type's transactions.
     */
    @GetMapping("/transactionTypes/{type}")
    @Operation(summary = "Export a single transaction type")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> exportSingleType(@RequestParam String destination, @PathVariable String type) {
        return typeExportService.launchSingleTransactionTypeJob(destination, type);
    }
}
