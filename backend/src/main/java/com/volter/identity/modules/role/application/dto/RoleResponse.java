package com.volter.identity.modules.role.application.dto;

import java.time.OffsetDateTime;

public record RoleResponse(
        Long id,
        String name,
        int permissionCount,
        OffsetDateTime createdAt
) {
}
