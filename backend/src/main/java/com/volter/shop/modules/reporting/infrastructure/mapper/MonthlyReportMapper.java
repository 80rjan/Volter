package com.volter.shop.modules.reporting.infrastructure.mapper;

import com.volter.shop.modules.reporting.domain.model.MonthlyReport;
import com.volter.shop.modules.reporting.domain.model.MonthlyReportBreakdown;
import com.volter.shop.modules.reporting.web.response.MonthlyReportDetailedResponse;
import com.volter.shop.modules.reporting.web.response.MonthlyReportResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {MonthlyReportBreakdownMapper.class})
public abstract class MonthlyReportMapper {

    public abstract MonthlyReportResponse toResponse(MonthlyReport monthlyReport);

    public abstract MonthlyReportDetailedResponse toDetailedResponse(MonthlyReport monthlyReport);

    @AfterMapping
    protected void afterMappingResponse(
            MonthlyReport report,
            @MappingTarget MonthlyReportResponse response
    ) {
        response.setTurnover(report.getBreakdowns().stream().mapToInt(MonthlyReportBreakdown::getTurnover).sum());
        response.setCashOut(report.getBreakdowns().stream().mapToInt(MonthlyReportBreakdown::getCashOut).sum());
        response.setRevenue(report.getBreakdowns().stream().mapToInt(MonthlyReportBreakdown::getRevenue).sum());
        response.setGrossProfit(report.getBreakdowns().stream().mapToInt(MonthlyReportBreakdown::getGrossProfit).sum());
        response.setExpenses(report.getBreakdowns().stream().mapToInt(MonthlyReportBreakdown::getExpenses).sum());
        response.setNetProfit(report.getBreakdowns().stream().mapToInt(MonthlyReportBreakdown::getNetProfit).sum());
    }
}
