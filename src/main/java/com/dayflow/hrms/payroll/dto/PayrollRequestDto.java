package com.dayflow.hrms.payroll.dto;

import com.dayflow.hrms.payroll.model.PaymentStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for creating and updating Payroll/Salary records.
 */
public class PayrollRequestDto {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Basic salary must be non-negative")
    private BigDecimal basicSalary;

    @DecimalMin(value = "0.0", inclusive = true, message = "Allowances must be non-negative")
    private BigDecimal allowances = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Deductions must be non-negative")
    private BigDecimal deductions = BigDecimal.ZERO;

    @NotNull(message = "Pay period month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer payPeriodMonth;

    @NotNull(message = "Pay period year is required")
    @Min(value = 2000, message = "Year must be valid")
    private Integer payPeriodYear;

    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private LocalDate paymentDate;

    private String remarks;

    public PayrollRequestDto() {
    }

    public PayrollRequestDto(Long employeeId, BigDecimal basicSalary, BigDecimal allowances, BigDecimal deductions, Integer payPeriodMonth, Integer payPeriodYear, PaymentStatus paymentStatus, String remarks) {
        this.employeeId = employeeId;
        this.basicSalary = basicSalary;
        this.allowances = allowances;
        this.deductions = deductions;
        this.payPeriodMonth = payPeriodMonth;
        this.payPeriodYear = payPeriodYear;
        this.paymentStatus = paymentStatus;
        this.remarks = remarks;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getAllowances() {
        return allowances;
    }

    public void setAllowances(BigDecimal allowances) {
        this.allowances = allowances;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public Integer getPayPeriodMonth() {
        return payPeriodMonth;
    }

    public void setPayPeriodMonth(Integer payPeriodMonth) {
        this.payPeriodMonth = payPeriodMonth;
    }

    public Integer getPayPeriodYear() {
        return payPeriodYear;
    }

    public void setPayPeriodYear(Integer payPeriodYear) {
        this.payPeriodYear = payPeriodYear;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
