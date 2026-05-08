package com.volter.shop.modules.transaction.web.response;

import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionMarginType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransactionResponse {
    @NotNull(message = "Transaction ID is required")
    private Long id;

    @NotNull(message = "Transaction category is required")
    private TransactionCategory transactionCategory;

    @NotNull(message = "Transaction amount is required")
    private Integer amount;

    @NotNull(message = "Transaction direction is required")
    private TransactionDirection direction;

    @NotNull(message = "Transaction margin amount is required")
    private Integer marginAmount;

    @NotNull(message = "Transaction margin type is required")
    private TransactionMarginType marginType;

    @NotNull(message = "Transaction created at is required")
    private LocalDateTime createdAt;

    // nullable
    private String description;
}
