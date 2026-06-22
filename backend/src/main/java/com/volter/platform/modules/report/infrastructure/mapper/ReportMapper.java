package com.volter.platform.modules.report.infrastructure.mapper;

import com.volter.platform.modules.report.application.ReportMetrics;
import com.volter.platform.modules.report.application.dto.response.ReportDetailedResponse;
import com.volter.platform.modules.report.application.dto.response.ReportResponse;
import com.volter.platform.modules.report.domain.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReportMetrics.class)
public interface ReportMapper {

    // Headline figures are derived from the payload so the list row can show them
    // without fetching each report's full detail.
    @Mapping(target = "totalRevenue", expression = "java(ReportMetrics.totalRevenue(report.getPayload()))")
    @Mapping(target = "totalExpenses", expression = "java(ReportMetrics.totalExpenses(report.getPayload()))")
    @Mapping(target = "netProfit", expression = "java(ReportMetrics.netProfit(report.getPayload()))")
    @Mapping(target = "moneyGivenToClients", expression = "java(ReportMetrics.moneyGivenToClients(report.getPayload()))")
    ReportResponse toResponse(Report report);

    ReportDetailedResponse toDetailedResponse(Report report);
}
