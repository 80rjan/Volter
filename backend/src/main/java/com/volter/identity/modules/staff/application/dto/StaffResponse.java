package com.volter.identity.modules.staff.application.dto;

import com.volter.identity.modules.staff.domain.model.enums.StaffStatus;

import java.time.OffsetDateTime;

public record StaffResponse(
        Long id,
        String fullName,
        String username,
        StaffStatus status,
        Long managerId,
        OffsetDateTime createdAt,
        OffsetDateTime deletedAt
) {
}
