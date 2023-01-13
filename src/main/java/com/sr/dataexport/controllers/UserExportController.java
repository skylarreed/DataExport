package com.sr.dataexport.controllers;

import com.sr.dataexport.services.MerchantExportService;
import com.sr.dataexport.services.UserExportService;
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
     * @param outputPath
     * @param userId
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to expose the endpoint to export a single user's transactions.
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> exportSingleUserTransactions(@RequestParam("destination") String outputPath, @PathVariable long userId){
        return userExportService.exportSingleUserTransactions(outputPath, userId);
    }

    /**
     * @param outputPath
     * @return ResponseEntity<?> to indicate the status of the request.
     * @Description This method is used to expose the endpoint to export all users' transactions.
     */
    @GetMapping("/users")
    public ResponseEntity<?> exportAllUsersTransactions(@RequestParam("destination") String outputPath){
        return userExportService.exportAllUsersTransactions(outputPath);
    }


}
