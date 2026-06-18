package com.volter.identity.modules.role.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleUpdateRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 50)
        String name
) {
}
