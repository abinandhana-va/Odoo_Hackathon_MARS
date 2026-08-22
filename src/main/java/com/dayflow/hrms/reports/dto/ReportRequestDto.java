package com.dayflow.hrms.reports.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Placeholder DTO for report generation requests.
 */
public class ReportRequestDto {

    @NotBlank(message = "Report type is required (e.g., PAYSLIP, MONTHLY_PAYROLL, TAX_SUMMARY)")
    private String reportType;

    private Long employeeId;
    private Integer month;
    private Integer year;
    private String format = "PDF";

    public ReportRequestDto() {
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
