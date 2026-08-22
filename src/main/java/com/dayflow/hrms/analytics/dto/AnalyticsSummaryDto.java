package com.dayflow.hrms.analytics.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Placeholder DTO for future HR & Payroll Analytics.
 */
public class AnalyticsSummaryDto {

    private long totalEmployees;
    private BigDecimal totalMonthlyPayroll;
    private BigDecimal averageSalary;
    private Map<String, Long> departmentHeadcount;
    private String statusMessage = "Analytics module placeholder ready for future integration";

    public AnalyticsSummaryDto() {
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public BigDecimal getTotalMonthlyPayroll() {
        return totalMonthlyPayroll;
    }

    public void setTotalMonthlyPayroll(BigDecimal totalMonthlyPayroll) {
        this.totalMonthlyPayroll = totalMonthlyPayroll;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }

    public void setAverageSalary(BigDecimal averageSalary) {
        this.averageSalary = averageSalary;
    }

    public Map<String, Long> getDepartmentHeadcount() {
        return departmentHeadcount;
    }

    public void setDepartmentHeadcount(Map<String, Long> departmentHeadcount) {
        this.departmentHeadcount = departmentHeadcount;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
