package com.sr.dataexport.controllers;

import com.sr.dataexport.services.YearExportService;
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
 * @ClassName YearController
 * @Description This class is used to export the data based on the year.
 */
@RestController
public class YearController {

    YearExportService yearExportService;

    public YearController(YearExportService yearExportService) {
        this.yearExportService = yearExportService;
    }

    /**
     * @param destination
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to call the service to export all years' transactions.
     */
    @GetMapping("/years")
    @Operation(summary = "Export all years")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted, Job started"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, Job failed to start. Contact the administrator."),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> exportYears(@RequestParam String destination) {
        return yearExportService.launchAllYearsExportJob(destination);
    }

    /**
     * @param year
     * @param destination
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to call the service to export a single year's transactions.
     */
    @GetMapping("/years/{year}")
    @Operation(summary = "Export a single year")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted, Job started"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, Job failed to start. Contact the administrator."),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> exportSingleYear(@RequestParam String destination, @PathVariable String year) {
        return yearExportService.launchSingleYearExportJob(destination, year);
    }
}
