package com.volter.platform.modules.report.infrastructure.mapper;

import com.volter.platform.modules.report.application.dto.response.ReportDetailedResponse;
import com.volter.platform.modules.report.application.dto.response.ReportResponse;
import com.volter.platform.modules.report.domain.model.Report;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportResponse toResponse(Report report);

    ReportDetailedResponse toDetailedResponse(Report report);
}
