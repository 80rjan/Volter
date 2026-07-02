package com.volter.shop.modules.notification.application.dto;

import com.volter.shop.modules.notification.domain.model.enums.NotificationType;

public record NotificationFilterRequest(
        NotificationType type,
        Boolean read
) {
}
