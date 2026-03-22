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
@DiscriminatorValue("RENEWED")
public class PawnRenewedEvent extends PawnEvent{

    @Column(nullable = false)
    private LocalDate originalMaturityDate;

    @Column(nullable = false)
    private LocalDate newMaturityDate;

    @Column(nullable = false)
    private Integer interestPaid;
}
