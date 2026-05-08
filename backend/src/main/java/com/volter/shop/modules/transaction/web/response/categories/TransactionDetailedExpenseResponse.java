package com.volter.shop.modules.transaction.web.response.categories;

import com.volter.shop.modules.expense.web.response.ExpenseResponse;
import com.volter.shop.modules.transaction.web.response.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.web.response.TransactionResponse;
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
