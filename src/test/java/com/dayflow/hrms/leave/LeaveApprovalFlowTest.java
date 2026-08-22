package com.dayflow.hrms.leave;

import com.dayflow.common.enums.Role;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.leave.dto.LeaveApprovalRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;
import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.model.LeaveStatus;
import com.dayflow.hrms.leave.model.LeaveType;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveApprovalFlowTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private NotificationService notificationService;

    private LeaveService leaveService;

    private Employee mockEmployee;
    private LeaveRequest mockPendingLeave;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveServiceImpl(leaveRepository, employeeRepository, notificationService);
        mockEmployee = new Employee(1L, "EMP001", "Jane Smith", "jane.smith@dayflow.internal", "password", Role.HR, LocalDateTime.now(), LocalDateTime.now());

        mockPendingLeave = new LeaveRequest(mockEmployee, LeaveType.PAID, LocalDate.now(), LocalDate.now().plusDays(3), "Annual vacation");
        mockPendingLeave.setId(100L);
        mockPendingLeave.setTotalDays(4);
        mockPendingLeave.setStatus(LeaveStatus.PENDING);
    }

    @Test
    @DisplayName("HR successfully approves pending leave request and triggers employee notification")
    void testHrApproveLeaveRequest() {
        when(leaveRepository.findById(100L)).thenReturn(Optional.of(mockPendingLeave));
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveApprovalRequestDto approvalDto = new LeaveApprovalRequestDto(LeaveStatus.APPROVED, "Approved by HR Manager");
        LeaveResponseDto result = leaveService.approveOrRejectLeave(100L, approvalDto, "hr.manager@dayflow.internal");

        assertNotNull(result);
        assertEquals(LeaveStatus.APPROVED, result.getStatus());
        assertEquals("Approved by HR Manager", result.getAdminComment());
        assertEquals("hr.manager@dayflow.internal", result.getApprovedBy());

        verify(leaveRepository, times(1)).save(mockPendingLeave);
        verify(notificationService, times(1)).createNotification(eq(mockEmployee), anyString(), eq(NotificationType.LEAVE_APPROVED));
    }

    @Test
    @DisplayName("HR successfully rejects pending leave request and triggers employee notification")
    void testHrRejectLeaveRequest() {
        when(leaveRepository.findById(100L)).thenReturn(Optional.of(mockPendingLeave));
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveApprovalRequestDto approvalDto = new LeaveApprovalRequestDto(LeaveStatus.REJECTED, "Insufficient leave balance");
        LeaveResponseDto result = leaveService.approveOrRejectLeave(100L, approvalDto, "hr.admin@dayflow.internal");

        assertNotNull(result);
        assertEquals(LeaveStatus.REJECTED, result.getStatus());
        assertEquals("Insufficient leave balance", result.getAdminComment());

        verify(leaveRepository, times(1)).save(mockPendingLeave);
        verify(notificationService, times(1)).createNotification(eq(mockEmployee), anyString(), eq(NotificationType.LEAVE_REJECTED));
    }

    @Test
    @DisplayName("HR retrieves all pending leave requests")
    void testGetPendingLeaveRequests() {
        when(leaveRepository.findByStatus(LeaveStatus.PENDING)).thenReturn(Collections.singletonList(mockPendingLeave));

        List<LeaveResponseDto> pendingList = leaveService.getPendingLeaveRequests();

        assertNotNull(pendingList);
        assertEquals(1, pendingList.size());
        assertEquals(LeaveStatus.PENDING, pendingList.get(0).getStatus());
    }
}
