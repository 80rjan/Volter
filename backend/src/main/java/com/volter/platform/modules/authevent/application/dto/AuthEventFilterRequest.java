package com.volter.platform.modules.authevent.application.dto;

import com.volter.platform.modules.authevent.domain.model.enums.AuthEventType;

import java.time.OffsetDateTime;

public record AuthEventFilterRequest(
        Long staffId,
        AuthEventType type,
        OffsetDateTime occurredFrom,
        OffsetDateTime occurredTo
) {
}
