package com.volter.shop.modules.inventory.application.dto;

import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import jakarta.validation.constraints.NotNull;

public record ItemStatusChangeRequest(
        @NotNull(message = "Target status is required") ItemStatus newStatus
) {
}
