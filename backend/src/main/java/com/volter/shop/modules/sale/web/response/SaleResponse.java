package com.volter.shop.modules.sale.web.response;

import com.volter.shop.modules.inventory.web.response.baseitem.ItemResponseData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponse{
    private Long id;
    private Integer purchasePrice;

    @NotNull(message = "Item response data is required")
    @Valid
    private ItemResponseData item;
}
