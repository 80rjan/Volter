package com.volter.backend.item.application.dtos;

import com.volter.backend.item.domain.model.enums.ItemType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemReportDTO {
    private ItemType itemType;
    private Integer numTransactions;
    private Integer totalValue;
    private Integer profit;
}
