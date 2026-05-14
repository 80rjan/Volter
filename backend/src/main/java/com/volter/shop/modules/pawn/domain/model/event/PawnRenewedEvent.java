package com.volter.shop.modules.pawn.domain.model.event;

import com.volter.shop.modules.pawn.domain.model.valueobject.MaturityDateChange;
import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
@DiscriminatorValue("RENEWED")
public class PawnRenewedEvent extends PawnEvent{

    @NotNull(message = "Maturity date change OV is required")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "oldMaturityDate", column = @Column(name = "old_maturity_date", nullable = true)),
            @AttributeOverride(name = "newMaturityDate", column = @Column(name = "new_maturity_date", nullable = true))
    })
    private MaturityDateChange maturityDateChange;

    @NotNull(message = "Interest paid is required")
    @AttributeOverride(name = "amount", column = @Column(name = "renewed_interest_paid", nullable = true))
    private Money interestPaid;
}
