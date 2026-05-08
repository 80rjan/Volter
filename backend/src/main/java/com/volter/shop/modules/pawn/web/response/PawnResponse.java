package com.volter.shop.modules.pawn.web.response;

import com.volter.shop.modules.inventory.web.response.baseitem.ItemResponseData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PawnResponse {
    private Long customerId;
    private String customerName;

    private Long id;
    private Integer amount;
    private Integer interest;
    private LocalDate issueDate;
    private LocalDate maturityDate;
    private Integer defaultDurationDays;

    @NotNull(message = "Item response data is required")
    @Valid
    private ItemResponseData item;
}
