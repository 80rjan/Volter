package com.volter.shop.modules.pawn.domain.model.event;

import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
@DiscriminatorValue("REDEEMED")
public class PawnRedeemedEvent extends PawnEvent{

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "total_amount_paid", nullable = false))
    private Money totalAmountPaid;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "redeemed_interest_paid", nullable = false))
    private Money interestPaid;
}
