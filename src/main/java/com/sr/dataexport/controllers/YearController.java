package com.sr.dataexport.controllers;

import com.sr.dataexport.services.YearExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YearController {

    YearExportService yearExportService;

    public YearController(YearExportService yearExportService) {
        this.yearExportService = yearExportService;
    }

    @GetMapping("/years")
    @Operation(summary = "Export all years")
    @SecurityRequirement(name = "bearerAuth")
    public void exportYears(@RequestParam String destination) {
        yearExportService.launchAllYearsExportJob(destination);
    }

    @GetMapping("/years/{year}")
    @Operation(summary = "Export a single year")
    @SecurityRequirement(name = "bearerAuth")
    public void exportSingleYear(@RequestParam String destination, @PathVariable String year) {
        yearExportService.launchSingleYearExportJob(destination, year);
    }
}
