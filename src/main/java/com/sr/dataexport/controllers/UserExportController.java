package com.sr.dataexport.controllers;

import com.sr.dataexport.services.MerchantExportService;
import com.sr.dataexport.services.UserExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author sr
 * @ClassName UserExportController
 * @Description This class is used to expose the user export endpoints.
 */
@RestController
public class UserExportController {
    private final UserExportService userExportService;

    private final MerchantExportService merchantExportService;



    public UserExportController(UserExportService userExportService, MerchantExportService merchantExportService) {
        this.userExportService = userExportService;
        this.merchantExportService = merchantExportService;
    }

    /**
     * @param destination
     * @param userId
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to expose the endpoint to export a single user's transactions.
     */
    @GetMapping("/users/{userId}")
    @Operation(summary = "Export a single user's transactions")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted, Job started"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, Job failed to start. Contact the administrator."),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> exportSingleUserTransactions(@RequestParam("destination") String destination,
                                                          @PathVariable long userId){
        return userExportService.exportSingleUserTransactions(destination, userId);
    }

    /**
     * @param destination
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to expose the endpoint to export all users' transactions.
     */
    @GetMapping("/users")
    public ResponseEntity<?> exportAllUsersTransactions(@RequestParam("destination") String destination){
        return userExportService.exportAllUsersTransactions(destination);
    }


}
