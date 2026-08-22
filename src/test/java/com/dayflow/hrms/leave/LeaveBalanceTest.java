package com.dayflow.hrms.leave;

import com.dayflow.common.enums.Role;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.common.exception.BadRequestException;
import com.dayflow.hrms.leave.dto.LeaveApprovalRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;
import com.dayflow.hrms.leave.dto.LeaveSummaryDto;
import com.dayflow.hrms.leave.model.LeaveBalance;
import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.model.LeaveStatus;
import com.dayflow.hrms.leave.model.LeaveType;
import com.dayflow.hrms.leave.repository.LeaveBalanceRepository;
import com.dayflow.hrms.leave.repository.LeaveRepository;
import com.dayflow.hrms.leave.service.LeaveService;
import com.dayflow.hrms.leave.service.impl.LeaveServiceImpl;
import com.dayflow.hrms.notification.model.NotificationType;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveBalanceTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private NotificationService notificationService;

    private LeaveService leaveService;

    private Employee mockEmployee;
    private LeaveBalance mockBalance;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveServiceImpl(leaveRepository, employeeRepository, leaveBalanceRepository, notificationService);

        mockEmployee = new Employee(1L, "EMP001", "Sam Wilson", "sam@dayflow.internal", "secret", Role.EMPLOYEE, LocalDateTime.now(), LocalDateTime.now());
        mockBalance = new LeaveBalance(mockEmployee, 15, 10);
    }

    @Test
    @DisplayName("Deduct leave balance upon HR approval of Paid Leave")
    void testBalanceDeductionOnApproval() {
        LeaveRequest leaveRequest = new LeaveRequest(mockEmployee, LeaveType.PAID, LocalDate.now(), LocalDate.now().plusDays(4), "Vacation");
        leaveRequest.setId(201L);
        leaveRequest.setTotalDays(5);
        leaveRequest.setStatus(LeaveStatus.PENDING);

        when(leaveRepository.findById(201L)).thenReturn(Optional.of(leaveRequest));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(leaveBalanceRepository.findByEmployeeId(1L)).thenReturn(Optional.of(mockBalance));
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(i -> i.getArgument(0));

        LeaveApprovalRequestDto approvalDto = new LeaveApprovalRequestDto(LeaveStatus.APPROVED, "Approved");
        LeaveResponseDto result = leaveService.approveOrRejectLeave(201L, approvalDto, "hr@dayflow.internal");

        assertNotNull(result);
        assertEquals(LeaveStatus.APPROVED, result.getStatus());
        assertEquals(10, mockBalance.getPaidLeaveBalance()); // 15 - 5 = 10

        verify(leaveBalanceRepository, times(1)).save(mockBalance);
        verify(notificationService, times(1)).createNotification(eq(mockEmployee), anyString(), eq(NotificationType.LEAVE_APPROVED));
    }

    @Test
    @DisplayName("Fail approval when employee has insufficient Paid Leave balance")
    void testInsufficientBalancePreventsApproval() {
        LeaveRequest leaveRequest = new LeaveRequest(mockEmployee, LeaveType.PAID, LocalDate.now(), LocalDate.now().plusDays(19), "Long Vacation");
        leaveRequest.setId(202L);
        leaveRequest.setTotalDays(20);
        leaveRequest.setStatus(LeaveStatus.PENDING);

        when(leaveRepository.findById(202L)).thenReturn(Optional.of(leaveRequest));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(leaveBalanceRepository.findByEmployeeId(1L)).thenReturn(Optional.of(mockBalance));

        LeaveApprovalRequestDto approvalDto = new LeaveApprovalRequestDto(LeaveStatus.APPROVED, "Approve");

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                leaveService.approveOrRejectLeave(202L, approvalDto, "hr@dayflow.internal")
        );

        assertTrue(ex.getMessage().contains("Insufficient Paid Leave balance"));
        assertEquals(15, mockBalance.getPaidLeaveBalance()); // Balance remains unchanged
    }

    @Test
    @DisplayName("Generate LOW_LEAVE_BALANCE notification when balance falls to 2 or fewer days")
    void testLowBalanceNotificationOnApproval() {
        mockBalance.setPaidLeaveBalance(3); // Only 3 days left

        LeaveRequest leaveRequest = new LeaveRequest(mockEmployee, LeaveType.PAID, LocalDate.now(), LocalDate.now().plusDays(1), "2 Days Off");
        leaveRequest.setId(203L);
        leaveRequest.setTotalDays(2);
        leaveRequest.setStatus(LeaveStatus.PENDING);

        when(leaveRepository.findById(203L)).thenReturn(Optional.of(leaveRequest));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(leaveBalanceRepository.findByEmployeeId(1L)).thenReturn(Optional.of(mockBalance));
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(i -> i.getArgument(0));

        LeaveApprovalRequestDto approvalDto = new LeaveApprovalRequestDto(LeaveStatus.APPROVED, "Approved");
        leaveService.approveOrRejectLeave(203L, approvalDto, "hr@dayflow.internal");

        assertEquals(1, mockBalance.getPaidLeaveBalance()); // 3 - 2 = 1 day left

        // Verify LOW_LEAVE_BALANCE alert was dispatched
        verify(notificationService, times(1)).createNotification(eq(mockEmployee), anyString(), eq(NotificationType.LOW_LEAVE_BALANCE));
    }

    @Test
    @DisplayName("Retrieve employee leave summary and statistics")
    void testGetLeaveSummary() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(leaveBalanceRepository.findByEmployeeId(1L)).thenReturn(Optional.of(mockBalance));
        when(leaveRepository.findByEmployeeIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());

        LeaveSummaryDto summary = leaveService.getLeaveSummaryByEmployeeId(1L);

        assertNotNull(summary);
        assertEquals(15, summary.getPaidLeaveBalance());
        assertEquals(10, summary.getSickLeaveBalance());
        assertEquals(0, summary.getTotalRequests());
    }
}
