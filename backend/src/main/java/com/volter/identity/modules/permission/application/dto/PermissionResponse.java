package com.volter.identity.modules.permission.application.dto;

import com.volter.identity.modules.permission.domain.model.enums.PermissionCategory;

import java.time.OffsetDateTime;

public record PermissionResponse(
        Long id,
        String name,
        PermissionCategory category,
        OffsetDateTime createdAt
) {
}
