package com.volter.backend.pawn.application.dto;

import com.volter.backend.customer.application.dto.CustomerDTO;
import com.volter.backend.item.application.dtos.ItemDetailsResponse;
import com.volter.backend.pawn.domain.model.enums.PawnStatus;
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
