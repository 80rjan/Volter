package com.volter.identity.modules.staff.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record StaffUpdateRequest(
        @NotBlank(message = "Primary phone is required") @Size(max = 32) String phonePrimary,
        @Size(max = 32) String phoneSecondary,
        @NotNull(message = "Base salary is required") @PositiveOrZero Integer baseSalary,
        @NotNull(message = "Bonus percent is required") @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal bonusPercent,
        Long managerId
) {
}
