package com.volter.shop.modules.pawn.domain.model;

import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import com.volter.shop.modules.pawn.domain.model.event.PawnEvent;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("PAWN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PawnTransaction extends Transaction {

    @NotNull(message = "Transaction action is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "pawn_action", nullable = true)
    private PawnTransactionAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pawn_id", nullable = true, foreignKey = @ForeignKey(name = "fk_transaction_pawn"))
    private Pawn pawn;

    @OneToOne(mappedBy = "transaction", fetch = FetchType.EAGER)
    private PawnEvent pawnEvent;

    @Override
    public TransactionCategory getCategory() {
        return TransactionCategory.PAWN;
    }
}