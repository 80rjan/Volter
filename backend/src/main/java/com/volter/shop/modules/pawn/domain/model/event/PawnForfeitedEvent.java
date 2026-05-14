package com.volter.shop.modules.pawn.domain.model.event;

import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
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

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "unpaid_amount", nullable = true))
    private Money unpaidAmount;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "unpaid_interest", nullable = true))
    private Money unpaidInterest;
}
