package com.dayflow.hrms.analytics.controller;

import com.dayflow.hrms.analytics.dto.AnalyticsSummaryDto;
import com.dayflow.hrms.analytics.service.AnalyticsService;
import com.dayflow.hrms.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Placeholder controller for Analytics API endpoints.
 */
@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics (Placeholder)", description = "Endpoints for HRMS metrics, headcount, and payroll analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get Analytics Summary", description = "Retrieves summary metrics for headcount and payroll")
    public ResponseEntity<ApiResponse<AnalyticsSummaryDto>> getSummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        AnalyticsSummaryDto summary = analyticsService.getPayrollAnalyticsSummary(month, year);
        return ResponseEntity.ok(ApiResponse.ok("Analytics summary placeholder retrieved", summary));
    }
}
