package com.sr.dataexport.controllers;

import com.sr.dataexport.services.StateExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class StateExportController {

    private final StateExportService stateExportService;

    public StateExportController(StateExportService stateExportService) {
        this.stateExportService = stateExportService;
    }

    @GetMapping("states/{state}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Export a single state's transactions")
    public ResponseEntity<?> exportSingleStateTransactions(@RequestParam String destination, @PathVariable String state) {
        return stateExportService.launchSingleStateTransactionsJob(destination, state);
    }

    @GetMapping("states")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Export all states' transactions")
    public ResponseEntity<?> exportAllStateTransactions(@RequestParam String destination) {
        return stateExportService.launchExportStateTransactionsJob(destination);
    }

}
