package com.volter.platform.modules.shop.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ShopCreateRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Code is required") @Size(max = 64) String code,
        @NotBlank(message = "Schema name is required")
        @Size(max = 63)
        @Pattern(regexp = "^[a-z][a-z0-9_]*$",
                message = "Schema name must be lowercase letters, digits and underscores starting with a letter")
        String schemaName,
        String address,
        @Size(max = 32) String phone
) {
}
