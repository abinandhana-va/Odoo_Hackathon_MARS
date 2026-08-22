package com.dayflow.hrms.payroll.model;

import com.dayflow.hrms.employee.model.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Payroll / Salary Entity.
 * Contains salary structure, allowances, deductions, and net salary calculations.
 */
@Entity
@Table(name = "salaries", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "pay_period_month", "pay_period_year"})
})
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Employee reference is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Basic salary must be non-negative")
    @Column(name = "basic_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "allowances", nullable = false, precision = 12, scale = 2)
    private BigDecimal allowances = BigDecimal.ZERO;

    @Column(name = "deductions", nullable = false, precision = 12, scale = 2)
    private BigDecimal deductions = BigDecimal.ZERO;

    @NotNull(message = "Net salary is required")
    @Column(name = "net_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "pay_period_month", nullable = false)
    private Integer payPeriodMonth;

    @Column(name = "pay_period_year", nullable = false)
    private Integer payPeriodYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Salary() {
    }

    public Salary(Employee employee, BigDecimal basicSalary, BigDecimal allowances, BigDecimal deductions, Integer payPeriodMonth, Integer payPeriodYear) {
        this.employee = employee;
        this.basicSalary = basicSalary;
        this.allowances = allowances != null ? allowances : BigDecimal.ZERO;
        this.deductions = deductions != null ? deductions : BigDecimal.ZERO;
        this.netSalary = calculateNetSalary();
        this.payPeriodMonth = payPeriodMonth;
        this.payPeriodYear = payPeriodYear;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.allowances == null) {
            this.allowances = BigDecimal.ZERO;
        }
        if (this.deductions == null) {
            this.deductions = BigDecimal.ZERO;
        }
        if (this.paymentStatus == null) {
            this.paymentStatus = PaymentStatus.PENDING;
        }
        this.netSalary = calculateNetSalary();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.netSalary = calculateNetSalary();
    }

    /**
     * Calculates net salary: netSalary = basicSalary + allowances - deductions
     */
    public BigDecimal calculateNetSalary() {
        BigDecimal basic = this.basicSalary != null ? this.basicSalary : BigDecimal.ZERO;
        BigDecimal allow = this.allowances != null ? this.allowances : BigDecimal.ZERO;
        BigDecimal ded = this.deductions != null ? this.deductions : BigDecimal.ZERO;
        return basic.add(allow).subtract(ded);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
        this.netSalary = calculateNetSalary();
    }

    public BigDecimal getAllowances() {
        return allowances;
    }

    public void setAllowances(BigDecimal allowances) {
        this.allowances = allowances;
        this.netSalary = calculateNetSalary();
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
        this.netSalary = calculateNetSalary();
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
