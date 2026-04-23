package com.volter.shop.modules.transaction.application.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.volter.shop.modules.transaction.application.dto.response.categories.TransactionDetailedCashRegisterResponse;
import com.volter.shop.modules.transaction.application.dto.response.categories.TransactionDetailedExpenseResponse;
import com.volter.shop.modules.transaction.application.dto.response.categories.TransactionDetailedPawnResponse;
import com.volter.shop.modules.transaction.application.dto.response.categories.TransactionDetailedSaleResponse;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "transactionCategory"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TransactionDetailedPawnResponse.class, name = TransactionCategory.PAWN_VALUE),
        @JsonSubTypes.Type(value = TransactionDetailedSaleResponse.class, name = TransactionCategory.SALE_VALUE),
        @JsonSubTypes.Type(value = TransactionDetailedExpenseResponse.class, name = TransactionCategory.EXPENSE_VALUE),
        @JsonSubTypes.Type(value = TransactionDetailedCashRegisterResponse.class, name = TransactionCategory.CASH_REGISTER_VALUE)
})
public interface TransactionDetailedResponse {
    TransactionCategory getTransactionCategory();
}
