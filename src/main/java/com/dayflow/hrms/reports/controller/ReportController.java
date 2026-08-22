package com.dayflow.hrms.reports.controller;

import com.dayflow.hrms.common.ApiResponse;
import com.dayflow.hrms.reports.dto.ReportRequestDto;
import com.dayflow.hrms.reports.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Placeholder controller for Reports API endpoints.
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports (Placeholder)", description = "Endpoints for generating payslips, salary summaries, and tax reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate Report", description = "Triggers placeholder report generation request")
    public ResponseEntity<ApiResponse<String>> generateReport(@Valid @RequestBody ReportRequestDto request) {
        String result = reportService.generateReport(request);
        return ResponseEntity.ok(ApiResponse.ok("Report generation request processed", result));
    }
}
