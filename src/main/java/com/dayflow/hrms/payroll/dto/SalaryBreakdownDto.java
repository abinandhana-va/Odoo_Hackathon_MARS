package com.dayflow.hrms.payroll.dto;

import java.math.BigDecimal;

public class SalaryBreakdownDto {
    private Long employeeId;
    private String employeeName;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal standardAllowance;
    private BigDecimal performanceBonus;
    private BigDecimal lta;
    private BigDecimal fixedAllowance;
    private BigDecimal providentFund;
    private BigDecimal professionalTax;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private Integer payPeriodMonth;
    private Integer payPeriodYear;

    public SalaryBreakdownDto() {}

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public BigDecimal getHra() { return hra; }
    public void setHra(BigDecimal hra) { this.hra = hra; }

    public BigDecimal getStandardAllowance() { return standardAllowance; }
    public void setStandardAllowance(BigDecimal standardAllowance) { this.standardAllowance = standardAllowance; }

    public BigDecimal getPerformanceBonus() { return performanceBonus; }
    public void setPerformanceBonus(BigDecimal performanceBonus) { this.performanceBonus = performanceBonus; }

    public BigDecimal getLta() { return lta; }
    public void setLta(BigDecimal lta) { this.lta = lta; }

    public BigDecimal getFixedAllowance() { return fixedAllowance; }
    public void setFixedAllowance(BigDecimal fixedAllowance) { this.fixedAllowance = fixedAllowance; }

    public BigDecimal getProvidentFund() { return providentFund; }
    public void setProvidentFund(BigDecimal providentFund) { this.providentFund = providentFund; }

    public BigDecimal getProfessionalTax() { return professionalTax; }
    public void setProfessionalTax(BigDecimal professionalTax) { this.professionalTax = professionalTax; }

    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }

    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }

    public Integer getPayPeriodMonth() { return payPeriodMonth; }
    public void setPayPeriodMonth(Integer payPeriodMonth) { this.payPeriodMonth = payPeriodMonth; }

    public Integer getPayPeriodYear() { return payPeriodYear; }
    public void setPayPeriodYear(Integer payPeriodYear) { this.payPeriodYear = payPeriodYear; }
}
