package com.dayflow.hrms.analytics.controller;

import com.dayflow.hrms.analytics.dto.AnalyticsSummaryDto;
import com.dayflow.hrms.analytics.dto.LeaveAnalyticsDto;
import com.dayflow.hrms.analytics.service.AnalyticsService;
import com.dayflow.hrms.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "HRMS Analytics", description = "Endpoints for payroll and leave analytics summaries")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/payroll")
    @Operation(summary = "Get payroll analytics summary")
    public ResponseEntity<ApiResponse<AnalyticsSummaryDto>> getPayrollAnalytics(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        AnalyticsSummaryDto dto = analyticsService.getPayrollAnalyticsSummary(month, year);
        return ResponseEntity.ok(ApiResponse.ok("Payroll analytics summary retrieved successfully", dto));
    }

    @GetMapping("/leave-stats")
    @Operation(summary = "Get leave management analytics and distribution stats")
    public ResponseEntity<ApiResponse<LeaveAnalyticsDto>> getLeaveAnalytics() {
        LeaveAnalyticsDto dto = analyticsService.getLeaveAnalyticsSummary();
        return ResponseEntity.ok(ApiResponse.ok("Leave analytics summary retrieved successfully", dto));
    }
}
