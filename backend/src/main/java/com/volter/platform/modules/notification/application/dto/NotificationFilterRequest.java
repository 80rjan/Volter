package com.volter.platform.modules.notification.application.dto;

import com.volter.platform.modules.notification.domain.model.enums.NotificationType;

public record NotificationFilterRequest(
        NotificationType type,
        Boolean read
) {
}
