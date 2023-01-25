package com.sr.dataexport.controllers;

import com.sr.dataexport.services.StateExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName StateExportController
 * @Description This class is used to export the data based on the state.
 */
@RestController
@Slf4j
public class StateExportController {

    private final StateExportService stateExportService;

    public StateExportController(StateExportService stateExportService) {
        this.stateExportService = stateExportService;
    }

    /**
     * @param state
     * @param destination
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to call the service to export a single state's transactions.
     */
    @GetMapping("states/{state}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Export a single state's transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted, Job started"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, Job failed to start. Contact the administrator."),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> exportSingleStateTransactions(@RequestParam String destination, @PathVariable String state) {
        return stateExportService.launchSingleStateTransactionsJob(destination, state);
    }

    /**
     * @param destination
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to call the service to export all states' transactions.
     */
    @GetMapping("states")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Export all states' transactions")
    public ResponseEntity<?> exportAllStateTransactions(@RequestParam String destination) {
        return stateExportService.launchExportStateTransactionsJob(destination);
    }

}
