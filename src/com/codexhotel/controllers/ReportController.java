package com.codexhotel.controllers;

import com.codexhotel.dtos.responses.ReportResponse;
import com.codexhotel.exceptions.AdminAccessRequiredException;
import com.codexhotel.exceptions.AdminNotFoundException;
import com.codexhotel.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<?> generateReport(@RequestParam String adminUserId) {
        try {
            ReportResponse response = reportService.generateReport(adminUserId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AdminNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AdminAccessRequiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}