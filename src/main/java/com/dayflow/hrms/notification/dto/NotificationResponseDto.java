package com.dayflow.hrms.notification.dto;

import com.dayflow.hrms.notification.model.Notification;
import com.dayflow.hrms.notification.model.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponseDto {

    private Long id;
    private Long recipientId;
    private String recipientName;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponseDto() {
    }

    public static NotificationResponseDto fromEntity(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(notification.getId());
        if (notification.getRecipient() != null) {
            dto.setRecipientId(notification.getRecipient().getId());
            dto.setRecipientName(notification.getRecipient().getName());
        }
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
