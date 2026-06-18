package com.volter.shop.modules.expense.domain.model;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseTransactionAction;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Subtype link giving a base {@link Transaction} its expense meaning, tying it
 * to the {@link Expense} it recorded. One-to-one with the transaction.
 */
@Entity
@Table(name = "expense_transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExpenseTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Transaction is required")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_expense_tx_transaction"))
    private Transaction transaction;

    @NotNull(message = "Expense is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false, foreignKey = @ForeignKey(name = "fk_expense_tx_expense"))
    private Expense expense;

    @NotNull(message = "Action is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    @Builder.Default
    private ExpenseTransactionAction action = ExpenseTransactionAction.EXPENSE_RECORDED;

    public static ExpenseTransaction record(Transaction transaction, Expense expense) {
        return ExpenseTransaction.builder()
                .transaction(transaction)
                .expense(expense)
                .action(ExpenseTransactionAction.EXPENSE_RECORDED)
                .build();
    }
}
