package com.volter.shop.modules.pawn.application.dto;

import com.volter.shop.modules.pawn.domain.model.enums.PawnContractNoteStatus;

import java.time.OffsetDateTime;

public record PawnContractNoteResponse(
        Long id,
        String description,
        PawnContractNoteStatus status,
        Long createdByStaffId,
        OffsetDateTime createdAt,
        OffsetDateTime resolvedAt
) {
}
