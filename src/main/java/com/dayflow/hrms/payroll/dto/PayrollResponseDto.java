package com.dayflow.hrms.payroll.dto;

import com.dayflow.hrms.payroll.model.PaymentStatus;
import com.dayflow.hrms.payroll.model.Salary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for returning detailed payroll and salary breakdown.
 */
public class PayrollResponseDto {

    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private BigDecimal basicSalary;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private Integer payPeriodMonth;
    private Integer payPeriodYear;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDate;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PayrollResponseDto() {
    }

    public static PayrollResponseDto fromEntity(Salary salary) {
        PayrollResponseDto dto = new PayrollResponseDto();
        dto.setId(salary.getId());
        if (salary.getEmployee() != null) {
            dto.setEmployeeId(salary.getEmployee().getId());
            dto.setEmployeeCode(salary.getEmployee().getEmployeeCode());
            dto.setEmployeeName(salary.getEmployee().getFirstName() + " " + salary.getEmployee().getLastName());
            dto.setDepartment(salary.getEmployee().getDepartment());
        }
        dto.setBasicSalary(salary.getBasicSalary());
        dto.setAllowances(salary.getAllowances());
        dto.setDeductions(salary.getDeductions());
        dto.setNetSalary(salary.getNetSalary());
        dto.setPayPeriodMonth(salary.getPayPeriodMonth());
        dto.setPayPeriodYear(salary.getPayPeriodYear());
        dto.setPaymentStatus(salary.getPaymentStatus());
        dto.setPaymentDate(salary.getPaymentDate());
        dto.setRemarks(salary.getRemarks());
        dto.setCreatedAt(salary.getCreatedAt());
        dto.setUpdatedAt(salary.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
