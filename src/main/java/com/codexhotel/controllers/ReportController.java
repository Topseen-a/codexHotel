package com.codexhotel.controllers;

import com.codexhotel.dtos.responses.ApiResponse;
import com.codexhotel.dtos.responses.ReportResponse;
import com.codexhotel.security.SecurityUtils;
import com.codexhotel.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ApiResponse<ReportResponse> generateReport(Authentication authentication) {
        ReportResponse response = reportService.generateReport(SecurityUtils.currentUserId(authentication));
        return ApiResponse.success(response);
    }
}
