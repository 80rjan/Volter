package com.volter.backend.pawn.domain.model.event;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
@DiscriminatorValue("REDEEMED")
public class PawnRedeemedEvent extends PawnEvent{

    @Column(nullable = false)
    private Integer amountPaid;

    @Column(nullable = false)
    private Integer interestPaid;
}
