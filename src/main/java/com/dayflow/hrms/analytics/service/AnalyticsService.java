package com.dayflow.hrms.analytics.service;

import com.dayflow.hrms.analytics.dto.AnalyticsSummaryDto;
import com.dayflow.hrms.analytics.dto.LeaveAnalyticsDto;

public interface AnalyticsService {

    AnalyticsSummaryDto getPayrollAnalyticsSummary(Integer month, Integer year);

    LeaveAnalyticsDto getLeaveAnalyticsSummary();
}
