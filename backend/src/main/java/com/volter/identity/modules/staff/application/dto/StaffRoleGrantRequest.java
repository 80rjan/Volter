package com.volter.identity.modules.staff.application.dto;

import jakarta.validation.constraints.NotNull;

public record StaffRoleGrantRequest(
        @NotNull(message = "Role id is required") Long roleId,
        @NotNull(message = "Shop id is required") Long shopId
) {
}
