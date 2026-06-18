package com.volter.platform.modules.authevent.application.dto;

import com.volter.platform.modules.authevent.domain.model.enums.AuthEventType;

import java.time.OffsetDateTime;

public record AuthEventResponse(
        Long id,
        Long staffId,
        AuthEventType type,
        String ipAddress,
        String userAgent,
        OffsetDateTime occurredAt
) {
}
