package com.volter.shop.modules.cashregister.application.dto;

import com.volter.identity.modules.staff.application.dto.StaffResponse;

/**
 * A cash register session together with the staff member who operated it.
 * Discrepancies are not included here — they are read through their own
 * permission-gated endpoint.
 */
public record CashRegisterSessionDetailedResponse(
        CashRegisterSessionResponse session,
        StaffResponse staff
) {
}
