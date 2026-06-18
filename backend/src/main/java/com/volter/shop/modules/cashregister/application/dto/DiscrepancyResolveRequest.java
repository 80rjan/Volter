package com.volter.shop.modules.cashregister.application.dto;

import jakarta.validation.constraints.NotBlank;

public record DiscrepancyResolveRequest(
        @NotBlank(message = "Resolution note is required") String resolutionNote
) {
}
