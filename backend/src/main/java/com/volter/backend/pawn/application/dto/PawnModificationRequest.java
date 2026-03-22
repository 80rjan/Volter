package com.volter.backend.pawn.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PawnModificationRequest {

    private Integer amount;

    private Integer interest;

    private Integer durationDays;

    private String itemDescription;

    private Float goldItemDetailsWeightGrams;
}
