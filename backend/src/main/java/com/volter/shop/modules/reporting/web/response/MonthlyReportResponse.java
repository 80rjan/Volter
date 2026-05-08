package com.volter.shop.modules.reporting.web.response;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MonthlyReportResponse {

    @NotNull(message = "Id is required")
    private Long id;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Month is required")
    private Integer month;

    @NotNull(message = "Turnover is required")
    private Integer turnover;

    @NotNull(message = "Cash out is required")
    private Integer cashOut;

    @NotNull(message = "Revenue is required")
    private Integer revenue;

    @NotNull(message = "Gross profit is required")
    private Integer grossProfit;

    @NotNull(message = "Expenses is required")
    private Integer expenses;

    @NotNull(message = "Net profit is required")
    private Integer netProfit;
}