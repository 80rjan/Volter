package com.volter.identity.modules.staff.application.dto;

import com.volter.identity.modules.staff.domain.model.enums.StaffStatus;

public record StaffFilterRequest(
        String fullName,
        String username,
        StaffStatus status,
        Boolean includeDeleted
) {
}
