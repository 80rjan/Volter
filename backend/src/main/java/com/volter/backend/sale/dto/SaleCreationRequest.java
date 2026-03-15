package com.volter.backend.sale.dto;

import com.volter.backend.customer.dto.CustomerCreationRequest;
import com.volter.backend.item.dto.ItemCreationRequest;
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
