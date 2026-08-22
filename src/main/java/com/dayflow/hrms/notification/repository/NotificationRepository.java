package com.dayflow.hrms.notification.repository;

import com.dayflow.hrms.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("hrmsNotificationRepository")
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);
    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);
    long countByRecipientIdAndIsReadFalse(Long recipientId);
}
