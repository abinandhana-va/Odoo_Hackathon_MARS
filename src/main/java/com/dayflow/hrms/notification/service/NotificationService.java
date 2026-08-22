package com.dayflow.hrms.notification.service;

import com.dayflow.employee.entity.Employee;
import com.dayflow.hrms.notification.dto.NotificationResponseDto;
import com.dayflow.hrms.notification.model.NotificationType;

import java.util.List;

public interface NotificationService {

    NotificationResponseDto createNotification(Employee recipient, String message, NotificationType type);

    NotificationResponseDto createNotification(Long recipientId, String message, NotificationType type);

    List<NotificationResponseDto> getNotificationsByRecipientId(Long recipientId);

    List<NotificationResponseDto> getUnreadNotificationsByRecipientId(Long recipientId);

    long getUnreadCount(Long recipientId);

    NotificationResponseDto markAsRead(Long notificationId);

    void markAllAsRead(Long recipientId);
}
