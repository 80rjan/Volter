package com.volter.shop.modules.pawn.domain.model;

import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * A single extension of a {@link PawnContract}: the due date moved from
 * {@code previousDueDate} to {@code newDueDate} in exchange for paid interest
 * (and an optional fee).
 */
@Entity
@Table(name = "pawn_contract_extension")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PawnContractExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pawn contract is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pawn_contract_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pce_pawn_contract"))
    private PawnContract pawnContract;

    @NotNull(message = "Previous due date is required")
    @Column(name = "previous_due_date", nullable = false)
    private LocalDate previousDueDate;

    @NotNull(message = "New due date is required")
    @Column(name = "new_due_date", nullable = false)
    private LocalDate newDueDate;

    @NotNull(message = "Interest paid is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "interest_paid", nullable = false))
    private Money interestPaid;

    @NotNull(message = "Fee is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "fee", nullable = false))
    @Builder.Default
    private Money fee = new Money(0);

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    static PawnContractExtension of(PawnContract pawnContract,
                                    LocalDate previousDueDate,
                                    LocalDate newDueDate,
                                    Money interestPaid,
                                    Money fee) {
        return PawnContractExtension.builder()
                .pawnContract(pawnContract)
                .previousDueDate(previousDueDate)
                .newDueDate(newDueDate)
                .interestPaid(interestPaid)
                .fee(fee == null ? new Money(0) : fee)
                .build();
    }

    public Money totalPaid() {
        return interestPaid.add(fee);
    }
}
