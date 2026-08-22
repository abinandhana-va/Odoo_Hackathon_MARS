package com.dayflow.hrms.notification.service.impl;

import com.dayflow.common.response.ApiResponse;
import com.dayflow.employee.entity.Employee;
import com.dayflow.employee.repository.EmployeeRepository;
import com.dayflow.hrms.common.exception.BadRequestException;
import com.dayflow.hrms.common.exception.ResourceNotFoundException;
import com.dayflow.hrms.notification.dto.NotificationResponseDto;
import com.dayflow.hrms.notification.model.Notification;
import com.dayflow.hrms.notification.model.NotificationType;
import com.dayflow.hrms.notification.repository.NotificationRepository;
import com.dayflow.hrms.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, EmployeeRepository employeeRepository) {
        this.notificationRepository = notificationRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public NotificationResponseDto createNotification(Employee recipient, String message, NotificationType type) {
        if (recipient == null) {
            throw new BadRequestException("Recipient employee cannot be null");
        }
        Notification notification = new Notification(recipient, message, type != null ? type : NotificationType.GENERAL);
        Notification saved = notificationRepository.save(notification);
        return NotificationResponseDto.fromEntity(saved);
    }

    @Override
    public NotificationResponseDto createNotification(Long recipientId, String message, NotificationType type) {
        Employee recipient = employeeRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + recipientId));
        return createNotification(recipient, message, type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByRecipientId(Long recipientId) {
        if (!employeeRepository.existsById(recipientId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + recipientId);
        }
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId)
                .stream()
                .map(NotificationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUnreadNotificationsByRecipientId(Long recipientId) {
        if (!employeeRepository.existsById(recipientId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + recipientId);
        }
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(recipientId)
                .stream()
                .map(NotificationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long recipientId) {
        if (!employeeRepository.existsById(recipientId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + recipientId);
        }
        return notificationRepository.countByRecipientIdAndIsReadFalse(recipientId);
    }

    @Override
    public NotificationResponseDto markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notification.setRead(true);
        Notification updated = notificationRepository.save(notification);
        return NotificationResponseDto.fromEntity(updated);
    }

    @Override
    public void markAllAsRead(Long recipientId) {
        if (!employeeRepository.existsById(recipientId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + recipientId);
        }
        List<Notification> unreadList = notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(recipientId);
        for (Notification n : unreadList) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unreadList);
    }
}
