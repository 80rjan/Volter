package com.volter.shop.modules.sale.web.response;

import com.volter.shop.modules.customer.web.response.CustomerResponse;
import com.volter.shop.modules.inventory.web.response.baseitem.ItemDetailedResponseData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleDetailedResponse {

    @NotNull(message = "Customer response data is required")
    private Long id;

    @NotNull(message = "Customer response data is required")
    private Integer purchasePrice;

    @NotNull(message = "Customer response data is required")
    private LocalDateTime createdAt;

    @NotNull(message = "Customer response data is required")
    private LocalDateTime updatedAt;

    @NotNull(message = "Customer response data is required")
    private CustomerResponse customer;

    @NotNull(message = "Item response data is required")
    @Valid
    private ItemDetailedResponseData item;
}
