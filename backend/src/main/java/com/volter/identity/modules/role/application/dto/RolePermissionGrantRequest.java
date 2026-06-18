package com.volter.identity.modules.role.application.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record RolePermissionGrantRequest(
        @NotEmpty(message = "At least one permission id is required")
        Set<Long> permissionIds
) {
}
