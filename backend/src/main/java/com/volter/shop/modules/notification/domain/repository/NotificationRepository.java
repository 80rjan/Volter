package com.volter.shop.modules.notification.domain.repository;

import com.volter.shop.modules.notification.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    long countByRecipientStaffIdAndReadFalse(Long recipientStaffId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = :now WHERE n.recipientStaffId = :staffId AND n.read = false")
    int markAllAsRead(Long staffId, OffsetDateTime now);
}
