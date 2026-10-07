package com.codexhotel.service;

import com.codexhotel.dto.response.ReportResponse;

public interface ReportService {

    ReportResponse generateReport(String requesterId);
}
