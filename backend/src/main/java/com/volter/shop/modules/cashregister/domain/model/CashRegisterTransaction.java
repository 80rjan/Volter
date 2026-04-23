package com.volter.shop.modules.cashregister.domain.model;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterTransactionAction;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("CASH_REGISTER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CashRegisterTransaction extends Transaction {

    @NotNull(message = "Transaction action is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CashRegisterTransactionAction action;

    @Override
    public TransactionCategory getCategory() {
        return TransactionCategory.CASH_REGISTER;
    }
}
