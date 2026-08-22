package com.dayflow.hrms.notification.controller;

import com.dayflow.hrms.common.ApiResponse;
import com.dayflow.hrms.notification.dto.NotificationResponseDto;
import com.dayflow.hrms.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "HRMS Notifications", description = "Endpoints for employee notification history and unread status")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/recipient/{recipientId}")
    @Operation(summary = "Get all notifications for an employee")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getNotificationsByRecipientId(@PathVariable Long recipientId) {
        List<NotificationResponseDto> notifications = notificationService.getNotificationsByRecipientId(recipientId);
        return ResponseEntity.ok(ApiResponse.ok("Notifications retrieved successfully", notifications));
    }

    @GetMapping("/unread/{recipientId}")
    @Operation(summary = "Get all unread notifications for an employee")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getUnreadNotificationsByRecipientId(@PathVariable Long recipientId) {
        List<NotificationResponseDto> notifications = notificationService.getUnreadNotificationsByRecipientId(recipientId);
        return ResponseEntity.ok(ApiResponse.ok("Unread notifications retrieved successfully", notifications));
    }

    @GetMapping("/unread-count/{recipientId}")
    @Operation(summary = "Get count of unread notifications for an employee")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@PathVariable Long recipientId) {
        long count = notificationService.getUnreadCount(recipientId);
        return ResponseEntity.ok(ApiResponse.ok("Unread count retrieved successfully", Map.of("unreadCount", count)));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> markAsRead(@PathVariable Long id) {
        NotificationResponseDto updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", updated));
    }

    @PutMapping("/mark-all-read/{recipientId}")
    @Operation(summary = "Mark all notifications as read for an employee")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(@PathVariable Long recipientId) {
        notificationService.markAllAsRead(recipientId);
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read", "Success"));
    }
}
