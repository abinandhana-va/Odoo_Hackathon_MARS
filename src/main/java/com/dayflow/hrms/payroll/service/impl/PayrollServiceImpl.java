package com.dayflow.hrms.payroll.service.impl;

import com.dayflow.hrms.common.exception.BadRequestException;
import com.dayflow.hrms.common.exception.ResourceNotFoundException;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
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
import java.util.Optional;
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
    public PayrollResponseDto createPayroll(PayrollRequestDto requestDto) {
        return createOrUpdatePayroll(requestDto);
    }

    @Override
    public PayrollResponseDto createOrUpdatePayroll(PayrollRequestDto requestDto) {
        if (requestDto == null) {
            throw new BadRequestException("Payroll request cannot be empty");
        }

        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + requestDto.getEmployeeId()));

        Optional<Salary> existingOpt = payrollRepository.findByEmployeeIdAndPayPeriodMonthAndPayPeriodYear(
                requestDto.getEmployeeId(), requestDto.getPayPeriodMonth(), requestDto.getPayPeriodYear()
        );

        Salary salary = existingOpt.orElseGet(Salary::new);
        salary.setEmployee(employee);
        salary.setBasicSalary(requestDto.getBasicSalary());
        salary.setAllowances(requestDto.getAllowances());
        salary.setDeductions(requestDto.getDeductions());
        salary.setPayPeriodMonth(requestDto.getPayPeriodMonth());
        salary.setPayPeriodYear(requestDto.getPayPeriodYear());
        salary.setRemarks(requestDto.getRemarks());
        salary.setPaymentStatus(requestDto.getPaymentStatus() != null ? requestDto.getPaymentStatus() : PaymentStatus.PENDING);
        salary.calculateNetSalary();

        Salary saved = payrollRepository.save(salary);
        return PayrollResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PayrollResponseDto getPayrollById(Long id) {
        Salary salary = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary record not found with id: " + id));
        return PayrollResponseDto.fromEntity(salary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollResponseDto> getPayrollsByEmployeeId(Long employeeId) {
        return getPayrollByEmployeeId(employeeId);
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
                .orElseThrow(() -> new ResourceNotFoundException("Salary record not found with id: " + id));

        salary.setPaymentStatus(status);
        if (status == PaymentStatus.PAID) {
            salary.setPaymentDate(LocalDate.now());
        }

        Salary updated = payrollRepository.save(salary);
        return PayrollResponseDto.fromEntity(updated);
    }

    @Override
    public void deletePayroll(Long id) {
        if (!payrollRepository.existsById(id)) {
            throw new ResourceNotFoundException("Salary record not found with id: " + id);
        }
        payrollRepository.deleteById(id);
    }

    @Override
    public BigDecimal calculateNetSalary(BigDecimal basicSalary, BigDecimal allowances, BigDecimal deductions) {
        BigDecimal basic = basicSalary != null ? basicSalary : BigDecimal.ZERO;
        BigDecimal allow = allowances != null ? allowances : BigDecimal.ZERO;
        BigDecimal deduct = deductions != null ? deductions : BigDecimal.ZERO;
        return basic.add(allow).subtract(deduct);
    }
}
