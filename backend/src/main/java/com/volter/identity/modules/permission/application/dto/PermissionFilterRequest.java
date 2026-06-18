package com.volter.identity.modules.permission.application.dto;

import com.volter.identity.modules.permission.domain.model.enums.PermissionCategory;

public record PermissionFilterRequest(
        String name,
        PermissionCategory category
) {
}
