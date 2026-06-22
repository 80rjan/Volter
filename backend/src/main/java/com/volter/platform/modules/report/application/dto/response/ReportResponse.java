package com.volter.platform.modules.report.application.dto.response;

import com.volter.platform.modules.report.domain.model.enums.ReportType;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ReportResponse(
        Long id,
        Long shopId,
        Long ownerStaffId,
        Long subjectStaffId,
        ReportType type,
        LocalDate dateFrom,
        LocalDate dateTo,
        OffsetDateTime generatedAt,
        // Headline figures derived from the payload, so the list row can show them
        // without fetching each report's full detail.
        long totalRevenue,
        long totalExpenses,
        long netProfit,
        long moneyGivenToClients
) {
}
