package com.volter.shop.modules.transaction.application.dto.response.categories;

import com.volter.shop.modules.pawn.application.dto.response.PawnDetailedResponse;
import com.volter.shop.modules.transaction.application.dto.response.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.application.dto.response.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class TransactionDetailedPawnResponse extends TransactionResponse
        implements TransactionDetailedResponse {

    @NotNull(message = "Pawn response data is required")
    @Valid
    PawnDetailedResponse pawn;


    @Override
    public TransactionCategory getTransactionCategory() {
        return TransactionCategory.PAWN;
    }
}
