package com.volter.shop.modules.reporting.web.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MonthlyReportDetailedResponse {
    private Long id;
    private Integer year;
    private Integer month;

    private List<MonthlyReportBreakdownResponse> breakdowns;
}
