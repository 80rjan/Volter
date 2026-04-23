package com.volter.shop.modules.transaction.application.dto.response.categories;

import com.volter.shop.modules.expense.application.dto.response.ExpenseResponse;
import com.volter.shop.modules.transaction.application.dto.response.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.application.dto.response.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import jakarta.validation.constraints.NotNull;

public class TransactionDetailedExpenseResponse extends TransactionResponse implements TransactionDetailedResponse {

    @NotNull(message = "Expense details is required")
    public ExpenseResponse expense;

    @Override
    public TransactionCategory getTransactionCategory() {
        return TransactionCategory.EXPENSE;
    }
}
