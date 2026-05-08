package com.volter.shop.modules.reporting.infrastructure.mapper;

import com.volter.shop.modules.reporting.domain.model.MonthlyReportBreakdown;
import com.volter.shop.modules.reporting.web.response.MonthlyReportBreakdownResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MonthlyReportBreakdownMapper {

    MonthlyReportBreakdownResponse toResponse(MonthlyReportBreakdown breakdown);
}
