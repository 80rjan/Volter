package com.volter.platform.modules.notification.application.dto;

import com.volter.platform.modules.notification.domain.model.enums.NotificationType;

import java.time.OffsetDateTime;

public record NotificationResponse(
        Long id,
        Long recipientStaffId,
        Long shopId,
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
