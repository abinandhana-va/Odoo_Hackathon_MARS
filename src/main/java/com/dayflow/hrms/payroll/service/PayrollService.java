package com.dayflow.hrms.payroll.service;

import com.dayflow.hrms.payroll.dto.PayrollRequestDto;
import com.dayflow.hrms.payroll.dto.PayrollResponseDto;
import com.dayflow.hrms.payroll.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

public interface PayrollService {

    PayrollResponseDto createOrUpdatePayroll(PayrollRequestDto requestDto);

    PayrollResponseDto getPayrollById(Long id);

    List<PayrollResponseDto> getPayrollByEmployeeId(Long employeeId);

    List<PayrollResponseDto> getPayrollByPeriod(Integer month, Integer year);

    List<PayrollResponseDto> getAllPayrolls();

    PayrollResponseDto updatePaymentStatus(Long id, PaymentStatus status);

    void deletePayroll(Long id);

    BigDecimal calculateNetSalary(BigDecimal basicSalary, BigDecimal allowances, BigDecimal deductions);
}
