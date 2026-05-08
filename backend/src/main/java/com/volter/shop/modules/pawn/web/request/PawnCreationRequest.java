package com.volter.shop.modules.pawn.web.request;

import com.volter.shop.modules.customer.web.request.CustomerReferenceRequest;
import com.volter.shop.modules.inventory.web.request.baseitem.ItemReferenceRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PawnCreationRequest {
    @NotNull(message = "Amount is required")
    private Integer amount;

    @NotNull(message = "Interest is required")
    private Integer interest;

    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;

    @NotNull(message = "Maturity date is required")
    private LocalDate maturityDate;

    @NotNull(message = "Duration in days is required")
    private Integer durationDays;

    // could be blank or null
    private String transactionDescription;

    @NotNull(message = "Item creation request is required")
    @Valid
    private ItemReferenceRequest item;

    @NotNull(message = "Customer creation request is required")
    @Valid
    private CustomerReferenceRequest customer;
}
