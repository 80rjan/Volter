package com.volter.shop.modules.expense.domain.model;

import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("EXPENSE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExpenseTransaction extends Transaction {

    @NotNull(message = "Transaction expense is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = true, foreignKey = @ForeignKey(name = "fk_transaction_expense"))
    private Expense expense;

    @Override
    public TransactionCategory getCategory() {
        return TransactionCategory.EXPENSE;
    }
}