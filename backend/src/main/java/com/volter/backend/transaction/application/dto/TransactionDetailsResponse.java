package com.volter.backend.transaction.application.dto;

import com.volter.backend.transaction.domain.model.enums.TransactionAction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TransactionDetailsResponse {
    private TransactionAction action;

    private Integer cashIn;

    private Integer cashOut;

    private Integer profit;

    private LocalDateTime createdAt;

    private String description;
}
