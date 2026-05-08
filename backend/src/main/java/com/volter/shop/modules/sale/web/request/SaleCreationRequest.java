package com.volter.shop.modules.sale.web.request;

import com.volter.shop.modules.customer.web.request.CustomerReferenceRequest;
import com.volter.shop.modules.inventory.web.request.baseitem.ItemReferenceRequest;
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
