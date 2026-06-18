package com.volter.shop.modules.customer.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerUpdateRequest(
        @NotBlank(message = "Full name is required") String fullName,
        @NotBlank(message = "Primary phone is required") @Size(max = 32) String phonePrimary,
        @Size(max = 32) String phoneSecondary,
        @NotBlank(message = "Address is required") String address,
        @NotBlank(message = "City is required") @Size(max = 100) String city
) {
}
