package com.volter.backend.pawn.dto;

import com.volter.backend.customer.dto.CustomerDTO;
import com.volter.backend.item.dto.ItemDetailsResponse;
import com.volter.backend.pawn.enums.PawnStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PawnDetailsResponse {

    private Integer amount;

    private Integer interest;

    private LocalDate issueDate;

    private LocalDate maturityDate;

    private Integer durationDays;

    private PawnStatus status;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private CustomerDTO customer;

    private ItemDetailsResponse item;
}
