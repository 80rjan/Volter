package com.volter.shop.modules.pawn.application.dto;

import com.volter.shop.modules.customer.application.dto.CustomerResponse;
import com.volter.shop.modules.inventory.application.dto.ItemResponse;
import com.volter.shop.modules.pawn.domain.model.enums.PawnContractStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record PawnContractDetailedResponse(
        Long id,
        CustomerResponse customer,
        ItemResponse item,
        Long createdByStaffId,
        String createdByStaffName,
        Integer principalAmount,
        Integer interestAmount,
        Integer termDays,
        LocalDate issueDate,
        LocalDate dueDate,
        LocalDate originalDueDate,
        PawnContractStatus status,
        long daysOverdue,
        OffsetDateTime redeemedAt,
        OffsetDateTime forfeitedAt,
        List<PawnContractExtensionResponse> extensions,
        List<PawnNoteResponse> notes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
