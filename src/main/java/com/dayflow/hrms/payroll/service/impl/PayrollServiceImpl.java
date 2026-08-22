package com.dayflow.hrms.payroll.service.impl;

import com.dayflow.hrms.common.exception.BadRequestException;
import com.dayflow.hrms.common.exception.ResourceNotFoundException;
import com.dayflow.hrms.employee.model.Employee;
import com.dayflow.hrms.employee.repository.EmployeeRepository;
import com.dayflow.hrms.payroll.dto.PayrollRequestDto;
import com.dayflow.hrms.payroll.dto.PayrollResponseDto;
import com.dayflow.hrms.payroll.model.PaymentStatus;
import com.dayflow.hrms.payroll.model.Salary;
import com.dayflow.hrms.payroll.repository.PayrollRepository;
import com.dayflow.hrms.payroll.service.PayrollService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    public PayrollServiceImpl(PayrollRepository payrollRepository, EmployeeRepository employeeRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public PayrollResponseDto createOrUpdatePayroll(PayrollRequestDto requestDto) {
        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + requestDto.getEmployeeId()));

        Salary salary = payrollRepository.findByEmployeeIdAndPayPeriodMonthAndPayPeriodYear(
                requestDto.getEmployeeId(), requestDto.getPayPeriodMonth(), requestDto.getPayPeriodYear())
                .orElse(new Salary());

        salary.setEmployee(employee);
        salary.setBasicSalary(requestDto.getBasicSalary());
        salary.setAllowances(requestDto.getAllowances() != null ? requestDto.getAllowances() : BigDecimal.ZERO);
        salary.setDeductions(requestDto.getDeductions() != null ? requestDto.getDeductions() : BigDecimal.ZERO);
        salary.setNetSalary(calculateNetSalary(salary.getBasicSalary(), salary.getAllowances(), salary.getDeductions()));
        salary.setPayPeriodMonth(requestDto.getPayPeriodMonth());
        salary.setPayPeriodYear(requestDto.getPayPeriodYear());

        if (requestDto.getPaymentStatus() != null) {
            salary.setPaymentStatus(requestDto.getPaymentStatus());
        }
        if (requestDto.getPaymentDate() != null) {
            salary.setPaymentDate(requestDto.getPaymentDate());
        }
        if (requestDto.getRemarks() != null) {
            salary.setRemarks(requestDto.getRemarks());
        }

        Salary saved = payrollRepository.save(salary);
        return PayrollResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PayrollResponseDto getPayrollById(Long id) {
        Salary salary = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found with id: " + id));
        return PayrollResponseDto.fromEntity(salary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollResponseDto> getPayrollByEmployeeId(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        return payrollRepository.findByEmployeeId(employeeId)
                .stream()
                .map(PayrollResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollResponseDto> getPayrollByPeriod(Integer month, Integer year) {
        return payrollRepository.findByPayPeriodMonthAndPayPeriodYear(month, year)
                .stream()
                .map(PayrollResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollResponseDto> getAllPayrolls() {
        return payrollRepository.findAll()
                .stream()
                .map(PayrollResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PayrollResponseDto updatePaymentStatus(Long id, PaymentStatus status) {
        Salary salary = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found with id: " + id));
        salary.setPaymentStatus(status);
        if (status == PaymentStatus.PAID && salary.getPaymentDate() == null) {
            salary.setPaymentDate(LocalDate.now());
        }
        Salary updated = payrollRepository.save(salary);
        return PayrollResponseDto.fromEntity(updated);
    }

    @Override
    public void deletePayroll(Long id) {
        if (!payrollRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payroll record not found with id: " + id);
        }
        payrollRepository.deleteById(id);
    }

    @Override
    public BigDecimal calculateNetSalary(BigDecimal basicSalary, BigDecimal allowances, BigDecimal deductions) {
        BigDecimal basic = basicSalary != null ? basicSalary : BigDecimal.ZERO;
        BigDecimal allow = allowances != null ? allowances : BigDecimal.ZERO;
        BigDecimal ded = deductions != null ? deductions : BigDecimal.ZERO;
        return basic.add(allow).subtract(ded);
    }
}
