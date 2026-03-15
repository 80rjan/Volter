package com.volter.backend.pawn.dto;

import com.volter.backend.customer.dto.CustomerCreationRequest;
import com.volter.backend.item.dto.ItemCreationRequest;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PawnCreationRequest {
    private Integer amount;

    private Integer interest;

    private LocalDate issueDate;

    private LocalDate maturityDate;

    private Integer durationDays;

    private CustomerCreationRequest customer;

    private ItemCreationRequest item;
}
