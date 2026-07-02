package com.volter.identity.modules.staff.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record StaffCreateRequest(
        @NotBlank(message = "Full name is required") String fullName,
        @NotBlank(message = "Username is required") @Size(max = 100) String username,
        @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters") String password,
        @NotBlank(message = "National id is required") @Size(max = 32) String nationalId,
        @NotBlank(message = "Primary phone is required") @Size(max = 32) String phonePrimary,
        @Size(max = 32) String phoneSecondary,
        @NotNull(message = "Base salary is required") @PositiveOrZero Integer baseSalary,
        @NotNull(message = "Profit share percent is required") @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal profitSharePercent,
        Long managerId
) {
}
