package com.volter.shop.modules.notification.application.dto;

import com.volter.shop.modules.notification.domain.model.enums.NotificationType;

import java.time.OffsetDateTime;

public record NotificationResponse(
        Long id,
        Long recipientStaffId,
        NotificationType type,
        String title,
        String description,
        String entityType,
        Long entityId,
        boolean read,
        OffsetDateTime readAt,
        OffsetDateTime createdAt
) {
}
