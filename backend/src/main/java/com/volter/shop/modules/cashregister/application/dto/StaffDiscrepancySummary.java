package com.volter.shop.modules.cashregister.application.dto;

/**
 * Cash discrepancy totals for a single staff member's sessions over a period:
 * how many discrepancies and their signed sum (negative = net shortage).
 */
public record StaffDiscrepancySummary(
        Long count,
        Long totalDifference
) {
}
