package com.volter.platform.modules.report.application.dto.response;

import java.time.LocalDate;
import java.util.Map;

/**
 * An on-the-fly summary for an arbitrary date range — the same shape as a saved
 * monthly report, but computed per request and never persisted.
 */
public record PeriodReportResponse(
        LocalDate dateFrom,
        LocalDate dateTo,
        long totalRevenue,
        long totalExpenses,
        long netProfit,
        long moneyGivenToClients,
        Map<String, Object> payload
) {
}
