package com.dayflow.hrms;

import com.dayflow.common.enums.Role;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.ai.dto.AiQueryRequestDto;
import com.dayflow.hrms.ai.dto.AiQueryResponseDto;
import com.dayflow.hrms.ai.service.AiAssistantService;
import com.dayflow.hrms.ai.service.impl.AiAssistantServiceImpl;
import com.dayflow.hrms.attendance.dto.AttendanceResponseDto;
import com.dayflow.hrms.attendance.repository.AttendanceRepository;
import com.dayflow.hrms.attendance.service.AttendanceService;
import com.dayflow.hrms.attendance.service.impl.AttendanceServiceImpl;
import com.dayflow.hrms.leave.dto.LeaveApplicationRequestDto;
import com.dayflow.hrms.leave.dto.LeaveApprovalRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;
import com.dayflow.hrms.leave.model.LeaveBalance;
import com.dayflow.hrms.leave.model.LeaveRequest;
import com.dayflow.hrms.leave.model.LeaveStatus;
import com.dayflow.hrms.leave.model.LeaveType;
import com.dayflow.hrms.leave.repository.LeaveBalanceRepository;
import com.dayflow.hrms.leave.repository.LeaveRepository;
import com.dayflow.hrms.leave.service.LeaveService;
import com.dayflow.hrms.leave.service.impl.LeaveServiceImpl;
import com.dayflow.hrms.notification.service.NotificationService;
import com.dayflow.hrms.payroll.service.PayrollService;
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
class CompleteHRMSWorkflowTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PayrollService payrollService;

    private LeaveService leaveService;
    private AttendanceService attendanceService;
    private AiAssistantService aiAssistantService;

    private Employee mockEmployee;
    private LeaveBalance mockBalance;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveServiceImpl(leaveRepository, employeeRepository, leaveBalanceRepository, notificationService);
        attendanceService = new AttendanceServiceImpl(attendanceRepository, employeeRepository);
        aiAssistantService = new AiAssistantServiceImpl(leaveService, employeeRepository, attendanceService, payrollService);

        mockEmployee = new Employee(1L, "EMP001", "Alice Smith", "alice@dayflow.internal", "password", Role.EMPLOYEE, LocalDateTime.now(), LocalDateTime.now());
        mockBalance = new LeaveBalance(mockEmployee, 15, 10);
    }

    @Test
    @DisplayName("Complete Workflow: Check-In -> Leave Application -> HR Approval -> Balance Deduction -> AI Query")
    void testEndToEndHRMSWorkflow() {
        // 1. Check-In
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(attendanceRepository.findByEmployeeIdAndDate(eq(1L), any(LocalDate.class))).thenReturn(Optional.empty());
        when(attendanceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AttendanceResponseDto attResponse = attendanceService.checkIn(1L);
        assertNotNull(attResponse);

        // 2. Apply Leave
        when(leaveBalanceRepository.findByEmployeeId(1L)).thenReturn(Optional.of(mockBalance));
        when(leaveRepository.findByEmployeeIdAndStatusIn(eq(1L), anyList())).thenReturn(Collections.emptyList());

        LeaveRequest pendingRequest = new LeaveRequest(mockEmployee, LeaveType.PAID, LocalDate.now(), LocalDate.now().plusDays(2), "Vacation");
        pendingRequest.setId(501L);
        pendingRequest.setTotalDays(3);
        pendingRequest.setStatus(LeaveStatus.PENDING);

        when(leaveRepository.save(any(LeaveRequest.class))).thenReturn(pendingRequest);

        LeaveApplicationRequestDto appDto = new LeaveApplicationRequestDto(1L, LeaveType.PAID, LocalDate.now(), LocalDate.now().plusDays(2), "Vacation");
        LeaveResponseDto leaveApp = leaveService.applyLeave(appDto);
        assertNotNull(leaveApp);
        assertEquals(LeaveStatus.PENDING, leaveApp.getStatus());

        // 3. HR Approve
        when(leaveRepository.findById(501L)).thenReturn(Optional.of(pendingRequest));

        LeaveApprovalRequestDto approvalDto = new LeaveApprovalRequestDto(LeaveStatus.APPROVED, "Approved by HR");
        LeaveResponseDto approvedLeave = leaveService.approveOrRejectLeave(501L, approvalDto, "hr@dayflow.internal");

        assertEquals(LeaveStatus.APPROVED, approvedLeave.getStatus());
        assertEquals(12, mockBalance.getPaidLeaveBalance()); // 15 - 3 = 12

        // 4. AI Query
        when(leaveRepository.findByEmployeeIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.singletonList(pendingRequest));
        AiQueryResponseDto aiRes = aiAssistantService.processQuery(new AiQueryRequestDto("What is my leave balance?"));

        assertNotNull(aiRes);
        assertTrue(aiRes.getResponse().contains("12 Paid Leave days"));
    }
}
