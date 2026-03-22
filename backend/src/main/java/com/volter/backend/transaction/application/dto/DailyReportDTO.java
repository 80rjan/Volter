package com.volter.backend.transaction.application.dto;

import com.volter.backend.item.application.dtos.ItemReportDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DailyReportDTO {
    private Integer cashIn;
    private Integer cashOut;
    private Integer profit;

    private List<ItemReportDTO> itemReports;


}
