package com.volter.shop.modules.pawn.domain.model;

import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Subtype link giving a base {@link Transaction} its pawn meaning, tying it to a
 * {@link PawnContract} and the action that produced it (creation, redemption,
 * forfeiture or extension). One-to-one with the transaction.
 */
@Entity
@Table(name = "pawn_transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PawnTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Transaction is required")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_pawn_tx_transaction"))
    private Transaction transaction;

    @NotNull(message = "Pawn contract is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pawn_contract_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_tx_pawn_contract"))
    private PawnContract pawnContract;

    @NotNull(message = "Action is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private PawnTransactionAction action;

    public static PawnTransaction record(Transaction transaction, PawnContract pawnContract, PawnTransactionAction action) {
        return PawnTransaction.builder()
                .transaction(transaction)
                .pawnContract(pawnContract)
                .action(action)
                .build();
    }
}
