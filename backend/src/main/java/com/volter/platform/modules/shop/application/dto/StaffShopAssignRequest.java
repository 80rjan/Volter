package com.volter.platform.modules.shop.application.dto;

import jakarta.validation.constraints.NotNull;

public record StaffShopAssignRequest(
        @NotNull(message = "Staff id is required") Long staffId
) {
}
