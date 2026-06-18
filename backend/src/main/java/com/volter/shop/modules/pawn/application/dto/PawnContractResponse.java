package com.volter.shop.modules.pawn.application.dto;

import com.volter.shop.modules.inventory.application.dto.ItemResponse;
import com.volter.shop.modules.pawn.domain.model.enums.PawnContractStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record PawnContractResponse(
        Long id,
        Long customerId,
        String customerName,
        ItemResponse item,
        Long createdByStaffId,
        String createdByStaffName,
        Integer principalAmount,
        Integer interestAmount,
        Integer termDays,
        LocalDate issueDate,
        LocalDate dueDate,
        PawnContractStatus status,
        long daysOverdue,
        OffsetDateTime createdAt
) {
}
