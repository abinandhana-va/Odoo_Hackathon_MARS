package com.dayflow.hrms;

import com.dayflow.common.enums.Role;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.ai.dto.AiQueryRequestDto;
import com.dayflow.hrms.ai.dto.AiQueryResponseDto;
import com.dayflow.hrms.ai.service.AiAssistantService;
import com.dayflow.hrms.ai.service.impl.AiAssistantServiceImpl;
import com.dayflow.hrms.analytics.dto.LeaveAnalyticsDto;
import com.dayflow.hrms.analytics.service.AnalyticsService;
import com.dayflow.hrms.analytics.service.impl.AnalyticsServiceImpl;
import com.dayflow.hrms.attendance.service.AttendanceService;
import com.dayflow.hrms.common.exception.BadRequestException;
import com.dayflow.hrms.leave.dto.LeaveApplicationRequestDto;
import com.dayflow.hrms.leave.dto.LeaveResponseDto;
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
import com.dayflow.hrms.payroll.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinalSprintWorkflowTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private PayrollService payrollService;

    private LeaveService leaveService;
    private AnalyticsService analyticsService;
    private AiAssistantService aiAssistantService;

    private Employee mockEmployee;
    private LeaveBalance mockBalance;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveServiceImpl(leaveRepository, employeeRepository, leaveBalanceRepository, notificationService);
        analyticsService = new AnalyticsServiceImpl(leaveRepository);
        aiAssistantService = new AiAssistantServiceImpl(leaveService, employeeRepository, attendanceService, payrollService);

        mockEmployee = new Employee(1L, "EMP001", "Robert Vance", "robert@dayflow.internal", "password", Role.EMPLOYEE, LocalDateTime.now(), LocalDateTime.now());
        mockBalance = new LeaveBalance(mockEmployee, 15, 10);
    }

    @Test
    @DisplayName("Prevent overlapping leave requests for same employee")
    void testOverlappingLeavePrevention() {
        LocalDate existingStart = LocalDate.of(2026, 10, 10);
        LocalDate existingEnd = LocalDate.of(2026, 10, 15);

        LeaveRequest existingRequest = new LeaveRequest(mockEmployee, LeaveType.PAID, existingStart, existingEnd, "Existing Vacation");
        existingRequest.setId(301L);
        existingRequest.setTotalDays(6);
        existingRequest.setStatus(LeaveStatus.APPROVED);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(leaveRepository.findByEmployeeIdAndStatusIn(eq(1L), anyList())).thenReturn(Collections.singletonList(existingRequest));

        // Overlapping request: Oct 12 to Oct 18
        LeaveApplicationRequestDto newOverlappingDto = new LeaveApplicationRequestDto(
                1L, LeaveType.SICK, LocalDate.of(2026, 10, 12), LocalDate.of(2026, 10, 18), "Overlapping Request"
        );

        BadRequestException ex = assertThrows(BadRequestException.class, () -> leaveService.applyLeave(newOverlappingDto));
        assertTrue(ex.getMessage().contains("Overlapping leave request detected"));

        verify(leaveRepository, never()).save(any());
    }

    @Test
    @DisplayName("Generate LEAVE_SUBMITTED notification upon successful leave submission")
    void testLeaveSubmittedNotification() {
        LocalDate startDate = LocalDate.of(2026, 11, 1);
        LocalDate endDate = LocalDate.of(2026, 11, 3);

        LeaveApplicationRequestDto requestDto = new LeaveApplicationRequestDto(
                1L, LeaveType.PAID, startDate, endDate, "Conference"
        );

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(leaveBalanceRepository.findByEmployeeId(1L)).thenReturn(Optional.of(mockBalance));
        when(leaveRepository.findByEmployeeIdAndStatusIn(eq(1L), anyList())).thenReturn(Collections.emptyList());

        LeaveRequest savedRequest = new LeaveRequest(mockEmployee, LeaveType.PAID, startDate, endDate, "Conference");
        savedRequest.setId(401L);
        savedRequest.setTotalDays(3);

        when(leaveRepository.save(any(LeaveRequest.class))).thenReturn(savedRequest);

        LeaveResponseDto response = leaveService.applyLeave(requestDto);

        assertNotNull(response);
        assertEquals(401L, response.getId());

        verify(notificationService, times(1)).createNotification(eq(mockEmployee), anyString(), eq(NotificationType.LEAVE_SUBMITTED));
    }

    @Test
    @DisplayName("Retrieve real-time leave analytics and distribution summary")
    void testLeaveAnalyticsSummary() {
        LeaveRequest r1 = new LeaveRequest(mockEmployee, LeaveType.PAID, LocalDate.now(), LocalDate.now(), "R1");
        r1.setStatus(LeaveStatus.APPROVED);

        LeaveRequest r2 = new LeaveRequest(mockEmployee, LeaveType.SICK, LocalDate.now(), LocalDate.now(), "R2");
        r2.setStatus(LeaveStatus.REJECTED);

        LeaveRequest r3 = new LeaveRequest(mockEmployee, LeaveType.PAID, LocalDate.now(), LocalDate.now(), "R3");
        r3.setStatus(LeaveStatus.PENDING);

        when(leaveRepository.findAll()).thenReturn(Arrays.asList(r1, r2, r3));

        LeaveAnalyticsDto analytics = analyticsService.getLeaveAnalyticsSummary();

        assertNotNull(analytics);
        assertEquals(3, analytics.getTotalRequests());
        assertEquals(1, analytics.getPendingRequests());
        assertEquals(1, analytics.getApprovedRequests());
        assertEquals(1, analytics.getRejectedRequests());
        assertEquals(50.0, analytics.getApprovalRatePercentage()); // 1 approved out of 2 decided
    }

    @Test
    @DisplayName("AI Assistant retrieves accurate leave info from LeaveService")
    void testAiAssistantLeaveQuery() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(leaveBalanceRepository.findByEmployeeId(1L)).thenReturn(Optional.of(mockBalance));
        when(leaveRepository.findByEmployeeIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());

        AiQueryRequestDto queryDto = new AiQueryRequestDto("What is my current leave balance?");
        AiQueryResponseDto aiResponse = aiAssistantService.processQuery(queryDto);

        assertNotNull(aiResponse);
        assertTrue(aiResponse.getResponse().contains("15 Paid Leave days"));
        assertTrue(aiResponse.getResponse().contains("10 Sick Leave days"));
    }
}
