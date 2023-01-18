package com.sr.dataexport.controllers;

import com.sr.dataexport.services.TypeExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public class TypeExportController {

    private final TypeExportService typeExportService;

    public TypeExportController(TypeExportService typeExportService) {
        this.typeExportService = typeExportService;
    }

    @GetMapping("/transactionTypes")
    @Operation(summary = "Export all transaction types")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> exportAllType(@RequestParam String destination) {
        return typeExportService.launchTransactionTypeJob(destination);
    }

    @GetMapping("/transactionTypes/{type}")
    @Operation(summary = "Export a single transaction type")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> exportSingleType(@RequestParam String destination, @PathVariable String type) {
        return typeExportService.launchSingleTransactionTypeJob(destination, type);
    }
}
