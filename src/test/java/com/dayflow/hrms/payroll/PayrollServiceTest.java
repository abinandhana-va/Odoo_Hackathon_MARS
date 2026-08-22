package com.dayflow.hrms.payroll;

import com.dayflow.hrms.employee.model.Employee;
import com.dayflow.hrms.employee.repository.EmployeeRepository;
import com.dayflow.hrms.payroll.dto.PayrollRequestDto;
import com.dayflow.hrms.payroll.dto.PayrollResponseDto;
import com.dayflow.hrms.payroll.model.PaymentStatus;
import com.dayflow.hrms.payroll.model.Salary;
import com.dayflow.hrms.payroll.repository.PayrollRepository;
import com.dayflow.hrms.payroll.service.PayrollService;
import com.dayflow.hrms.payroll.service.impl.PayrollServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private PayrollService payrollService;

    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        payrollService = new PayrollServiceImpl(payrollRepository, employeeRepository);
        mockEmployee = new Employee(1L, "EMP001", "John", "Doe", "john.doe@dayflow.internal", "Engineering", "Senior Software Engineer");
    }

    @Test
    @DisplayName("Calculate Net Salary correctly: Net = Basic + Allowances - Deductions")
    void testCalculateNetSalary() {
        BigDecimal basic = new BigDecimal("5000.00");
        BigDecimal allowances = new BigDecimal("1200.00");
        BigDecimal deductions = new BigDecimal("300.00");

        BigDecimal netSalary = payrollService.calculateNetSalary(basic, allowances, deductions);

        assertEquals(new BigDecimal("5900.00"), netSalary);
    }

    @Test
    @DisplayName("Create Payroll successfully with proper salary breakdown")
    void testCreatePayroll() {
        PayrollRequestDto requestDto = new PayrollRequestDto();
        requestDto.setEmployeeId(1L);
        requestDto.setBasicSalary(new BigDecimal("6000.00"));
        requestDto.setAllowances(new BigDecimal("1000.00"));
        requestDto.setDeductions(new BigDecimal("500.00"));
        requestDto.setPayPeriodMonth(8);
        requestDto.setPayPeriodYear(2026);
        requestDto.setPaymentStatus(PaymentStatus.PENDING);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(payrollRepository.findByEmployeeIdAndPayPeriodMonthAndPayPeriodYear(1L, 8, 2026)).thenReturn(Optional.empty());

        Salary savedSalary = new Salary(mockEmployee, new BigDecimal("6000.00"), new BigDecimal("1000.00"), new BigDecimal("500.00"), 8, 2026);
        savedSalary.setId(100L);
        when(payrollRepository.save(any(Salary.class))).thenReturn(savedSalary);

        PayrollResponseDto result = payrollService.createOrUpdatePayroll(requestDto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(1L, result.getEmployeeId());
        assertEquals(new BigDecimal("6000.00"), result.getBasicSalary());
        assertEquals(new BigDecimal("1000.00"), result.getAllowances());
        assertEquals(new BigDecimal("500.00"), result.getDeductions());
        assertEquals(new BigDecimal("6500.00"), result.getNetSalary());
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());

        verify(payrollRepository, times(1)).save(any(Salary.class));
    }
}
