package com.volter.backend.pawn.dto;

import com.volter.backend.item.dto.ItemResponse;
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
    private Integer durationDays;

    private ItemResponse itemResponse;
}
