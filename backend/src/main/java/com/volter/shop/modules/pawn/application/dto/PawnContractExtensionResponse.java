package com.volter.shop.modules.pawn.application.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record PawnContractExtensionResponse(
        Long id,
        LocalDate previousDueDate,
        LocalDate newDueDate,
        Integer interestPaid,
        Integer fee,
        OffsetDateTime createdAt
) {
}
