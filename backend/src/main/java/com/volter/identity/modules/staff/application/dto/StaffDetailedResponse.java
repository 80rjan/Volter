package com.volter.identity.modules.staff.application.dto;

import com.volter.identity.modules.staff.domain.model.enums.StaffStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record StaffDetailedResponse(
        Long id,
        String fullName,
        String username,
        String nationalId,
        String phonePrimary,
        String phoneSecondary,
        Integer baseSalary,
        BigDecimal profitSharePercent,
        StaffStatus status,
        Long managerId,
        List<StaffRoleResponse> roleGrants,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime deletedAt
) {
}
