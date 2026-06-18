package com.volter.identity.modules.staff.application.dto;

import com.volter.identity.modules.role.application.dto.RoleDetailedResponse;
import com.volter.identity.modules.staff.domain.model.enums.StaffRoleStatus;
import com.volter.platform.modules.shop.application.dto.ShopResponse;

import java.time.OffsetDateTime;

public record StaffRoleResponse(
        Long id,
        RoleDetailedResponse role,
        ShopResponse shop,
        StaffRoleStatus status,
        OffsetDateTime grantedAt,
        OffsetDateTime revokedAt
) {
}
