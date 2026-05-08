package com.volter.shop.modules.pawn.web.response;

import com.volter.shop.modules.customer.web.response.CustomerResponse;
import com.volter.shop.modules.inventory.web.response.baseitem.ItemDetailedResponseData;
import com.volter.shop.modules.pawn.domain.model.enums.PawnStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PawnDetailedResponse {

    @NotNull(message = "Customer response data is required")
    private Long id;

    @NotNull(message = "Customer response data is required")
    private Integer amount;

    @NotNull(message = "Customer response data is required")
    private Integer interest;

    @NotNull(message = "Customer response data is required")
    private LocalDate issueDate;

    @NotNull(message = "Customer response data is required")
    private LocalDate maturityDate;

    @NotNull(message = "Customer response data is required")
    private Integer defaultDurationDays;

    @NotNull(message = "Customer response data is required")
    private PawnStatus status;

    private boolean active;

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
