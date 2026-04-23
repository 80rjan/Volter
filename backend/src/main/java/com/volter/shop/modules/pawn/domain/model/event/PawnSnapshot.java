package com.volter.shop.modules.pawn.domain.model.event;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Embeddable
public class PawnSnapshot {
    private Integer amount;
    private Integer interest;
    private Integer defaultDurationDays;
}
