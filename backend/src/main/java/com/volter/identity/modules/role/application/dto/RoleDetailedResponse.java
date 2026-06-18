package com.volter.identity.modules.role.application.dto;

import com.volter.identity.modules.permission.application.dto.PermissionResponse;

import java.time.OffsetDateTime;
import java.util.Set;

public record RoleDetailedResponse(
        Long id,
        String name,
        Set<PermissionResponse> permissions,
        OffsetDateTime createdAt
) {
}
