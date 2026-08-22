package com.dayflow.hrms.notification;

import com.dayflow.common.enums.Role;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.notification.dto.NotificationResponseDto;
import com.dayflow.hrms.notification.model.Notification;
import com.dayflow.hrms.notification.model.NotificationType;
import com.dayflow.hrms.notification.repository.NotificationRepository;
import com.dayflow.hrms.notification.service.NotificationService;
import com.dayflow.hrms.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private NotificationService notificationService;

    private Employee mockEmployee;
    private Notification mockNotification;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(notificationRepository, employeeRepository);
        mockEmployee = new Employee(1L, "EMP001", "Alice Doe", "alice@dayflow.internal", "password", Role.EMPLOYEE, LocalDateTime.now(), LocalDateTime.now());

        mockNotification = new Notification(mockEmployee, "Your leave request has been APPROVED", NotificationType.LEAVE_APPROVED);
        mockNotification.setId(50L);
    }

    @Test
    @DisplayName("Create notification successfully")
    void testCreateNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        NotificationResponseDto result = notificationService.createNotification(mockEmployee, "Your leave request has been APPROVED", NotificationType.LEAVE_APPROVED);

        assertNotNull(result);
        assertEquals(50L, result.getId());
        assertEquals("Alice Doe", result.getRecipientName());
        assertEquals(NotificationType.LEAVE_APPROVED, result.getType());
        assertFalse(result.isRead());

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Retrieve unread notification count for an employee")
    void testGetUnreadCount() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(notificationRepository.countByRecipientIdAndIsReadFalse(1L)).thenReturn(3L);

        long count = notificationService.getUnreadCount(1L);

        assertEquals(3L, count);
        verify(notificationRepository, times(1)).countByRecipientIdAndIsReadFalse(1L);
    }

    @Test
    @DisplayName("Mark notification as read successfully")
    void testMarkAsRead() {
        when(notificationRepository.findById(50L)).thenReturn(Optional.of(mockNotification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponseDto result = notificationService.markAsRead(50L);

        assertNotNull(result);
        assertTrue(result.isRead());
        verify(notificationRepository, times(1)).save(mockNotification);
    }
}
