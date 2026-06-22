package com.volter.identity.modules.staff.application.dto;

/**
 * A staff member's active assignment to one shop, together with the single role
 * they hold there (one role per shop). Role fields are null if the shop is
 * assigned but the role has been revoked.
 */
public record StaffShopAssignmentResponse(
        Long shopId,
        String shopName,
        String shopCode,
        Long staffRoleId,
        Long roleId,
        String roleName
) {
}
