package com.volter.shop.modules.inventory.application.dtos;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
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
