package com.dayflow.hrms.analytics.service;

import com.dayflow.hrms.analytics.dto.AnalyticsSummaryDto;

/**
 * Service placeholder for HR and Payroll Analytics.
 */
public interface AnalyticsService {
    AnalyticsSummaryDto getPayrollAnalyticsSummary(Integer month, Integer year);
}
