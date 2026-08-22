package com.dayflow.hrms.leave;

import com.dayflow.common.enums.Role;
import com.dayflow.hrms.common.exception.BadRequestException;
import com.dayflow.hrms.common.exception.ResourceNotFoundException;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.leave.dto.LeaveApplicationRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;
import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.model.LeaveStatus;
import com.dayflow.hrms.leave.model.LeaveType;
import com.dayflow.hrms.leave.repository.LeaveRepository;
import com.dayflow.hrms.leave.service.LeaveService;
import com.dayflow.hrms.leave.service.impl.LeaveServiceImpl;
import com.dayflow.hrms.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private NotificationService notificationService;

    private LeaveService leaveService;
    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveServiceImpl(leaveRepository, employeeRepository, notificationService);
        mockEmployee = new Employee(1L, "EMP001", "John Doe", "john.doe@dayflow.internal", "secret", Role.EMPLOYEE, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Successfully apply for leave with default PENDING status and valid day calculation")
    void testApplyLeaveSuccess() {
        LocalDate startDate = LocalDate.of(2026, 9, 1);
        LocalDate endDate = LocalDate.of(2026, 9, 5);

        LeaveApplicationRequestDto requestDto = new LeaveApplicationRequestDto(
                1L, LeaveType.PAID, startDate, endDate, "Vacation trip"
        );

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        LeaveRequest savedRequest = new LeaveRequest(mockEmployee, LeaveType.PAID, startDate, endDate, "Vacation trip");
        savedRequest.setId(10L);
        savedRequest.setTotalDays(5);
        savedRequest.setStatus(LeaveStatus.PENDING);

        when(leaveRepository.save(any(LeaveRequest.class))).thenReturn(savedRequest);

        LeaveResponseDto result = leaveService.applyLeave(requestDto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getEmployeeId());
        assertEquals(LeaveType.PAID, result.getLeaveType());
        assertEquals(LeaveStatus.PENDING, result.getStatus());
        assertEquals(5, result.getTotalDays());

        verify(leaveRepository, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    @DisplayName("Fail to apply for leave when end date is before start date")
    void testApplyLeaveEndDateBeforeStartDate() {
        LocalDate startDate = LocalDate.of(2026, 9, 5);
        LocalDate endDate = LocalDate.of(2026, 9, 1);

        LeaveApplicationRequestDto requestDto = new LeaveApplicationRequestDto(
                1L, LeaveType.SICK, startDate, endDate, "Invalid dates"
        );

        BadRequestException exception = assertThrows(BadRequestException.class, () -> leaveService.applyLeave(requestDto));
        assertEquals("End date cannot be before start date", exception.getMessage());

        verify(leaveRepository, never()).save(any());
    }

    @Test
    @DisplayName("Retrieve employee's leave history successfully")
    void testGetLeaveRequestsByEmployeeId() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        LeaveRequest leaveRequest = new LeaveRequest(mockEmployee, LeaveType.SICK, LocalDate.now(), LocalDate.now().plusDays(2), "Feeling unwell");
        leaveRequest.setId(12L);
        leaveRequest.setTotalDays(3);

        when(leaveRepository.findByEmployeeIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.singletonList(leaveRequest));

        List<LeaveResponseDto> result = leaveService.getLeaveRequestsByEmployeeId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(LeaveType.SICK, result.get(0).getLeaveType());
        assertEquals(3, result.get(0).getTotalDays());
    }
}
