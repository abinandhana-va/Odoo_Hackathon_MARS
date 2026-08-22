package com.dayflow.hrms.payroll;

import com.dayflow.common.enums.Role;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
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
import java.time.LocalDateTime;
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
        mockEmployee = new Employee(1L, "EMP001", "John Doe", "john.doe@dayflow.internal", "secret", Role.EMPLOYEE, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Calculate net salary correctly: Net = Basic + Allowances - Deductions")
    void testCalculateNetSalary() {
        Salary salary = new Salary(
                mockEmployee,
                new BigDecimal("50000.00"),
                new BigDecimal("10000.00"),
                new BigDecimal("5000.00"),
                8,
                2026
        );

        assertEquals(new BigDecimal("55000.00"), salary.getNetSalary());
    }

    @Test
    @DisplayName("Create payroll record successfully")
    void testCreatePayroll() {
        PayrollRequestDto requestDto = new PayrollRequestDto(
                1L,
                new BigDecimal("60000.00"),
                new BigDecimal("5000.00"),
                new BigDecimal("2000.00"),
                8,
                2026,
                PaymentStatus.PENDING,
                "August Payslip"
        );

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        Salary savedSalary = new Salary(
                mockEmployee,
                new BigDecimal("60000.00"),
                new BigDecimal("5000.00"),
                new BigDecimal("2000.00"),
                8,
                2026
        );
        savedSalary.setId(101L);
        savedSalary.setRemarks("August Payslip");

        when(payrollRepository.save(any(Salary.class))).thenReturn(savedSalary);

        PayrollResponseDto response = payrollService.createPayroll(requestDto);

        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertEquals(new BigDecimal("63000.00"), response.getNetSalary());
        verify(payrollRepository, times(1)).save(any(Salary.class));
    }
}
