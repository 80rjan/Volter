package com.volter.identity.modules.staff.application.dto;

/**
 * Minimal staff identity for pickers (e.g. the "filter by staff member" dropdown,
 * limited to the caller's own team).
 */
public record StaffOptionResponse(
        Long id,
        String fullName
) {
}
