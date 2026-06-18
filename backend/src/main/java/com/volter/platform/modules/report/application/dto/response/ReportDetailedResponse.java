package com.volter.platform.modules.report.application.dto.response;

import com.volter.platform.modules.report.domain.model.enums.ReportType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;

public record ReportDetailedResponse(
        Long id,
        Long shopId,
        Long ownerStaffId,
        Long subjectStaffId,
        ReportType type,
        LocalDate dateFrom,
        LocalDate dateTo,
        Map<String, Object> payload,
        OffsetDateTime generatedAt
) {
}
