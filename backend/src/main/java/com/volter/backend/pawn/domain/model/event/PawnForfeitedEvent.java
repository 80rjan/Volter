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
@DiscriminatorValue("FORFEITED")
public class PawnForfeitedEvent extends PawnEvent{

    @Column(nullable = false)
    private LocalDate maturityDate;

    @Column(nullable = false)
    private Integer unpaidAmount;

    @Column(nullable = false)
    private Integer unpaidInterest;
}
