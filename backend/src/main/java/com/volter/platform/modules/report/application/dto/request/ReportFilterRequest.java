package com.volter.platform.modules.report.application.dto.request;

import com.volter.platform.modules.report.domain.model.enums.ReportType;

import java.time.LocalDate;

public record ReportFilterRequest(
        ReportType type,
        Long shopId,
        Long subjectStaffId,
        LocalDate dateFrom,
        LocalDate dateTo
) {
}
