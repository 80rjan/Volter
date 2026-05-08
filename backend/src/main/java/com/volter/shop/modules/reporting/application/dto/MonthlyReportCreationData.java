package com.volter.shop.modules.reporting.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record MonthlyReportCreationData(
        @NotNull(message = "Year is required") Integer year,
        @NotNull(message = "Month is required") Integer month,

        @NotNull(message = "Generated at is required") List<MonthlyReportItemBreakdownCreationData> itemBreakdowns
        ) {
}
