package com.volter.backend.sale.application.dto;

import com.volter.backend.customer.application.dto.CustomerCreationRequest;
import com.volter.backend.item.application.dtos.ItemCreationRequest;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleCreationRequest {

    private Integer purchasePrice;

    private CustomerCreationRequest customer;

    private ItemCreationRequest item;
}
