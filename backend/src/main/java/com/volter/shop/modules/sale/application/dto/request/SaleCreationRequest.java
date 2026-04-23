package com.volter.shop.modules.sale.application.dto.request;

import com.volter.shop.modules.customer.application.dto.request.CustomerReferenceRequest;
import com.volter.shop.modules.inventory.application.dtos.baseitem.request.ItemReferenceRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleCreationRequest {

    private Integer purchasePrice;

    // could be blank or null
    private String transactionDescription;

    @NotNull(message = "Item creation request is required")
    @Valid
    private ItemReferenceRequest item;

    @NotNull(message = "Customer creation request is required")
    @Valid
    private CustomerReferenceRequest customer;
}
