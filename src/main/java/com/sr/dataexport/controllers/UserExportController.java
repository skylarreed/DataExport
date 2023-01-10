package com.sr.dataexport.controllers;

import com.sr.dataexport.services.UserExportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserExportController {
    private final UserExportService userExportService;



    public UserExportController(UserExportService userExportService) {
        this.userExportService = userExportService;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> exportSingleUserTransactions(@RequestParam("destination") String outputPath, @PathVariable long userId){
        return userExportService.exportSingleUserTransactions(outputPath, userId);
    }

    @GetMapping("/users")
    public ResponseEntity<?> exportAllUsersTransactions(@RequestParam("destination") String outputPath){
        return userExportService.exportAllUsersTransactions(outputPath);
    }

}
