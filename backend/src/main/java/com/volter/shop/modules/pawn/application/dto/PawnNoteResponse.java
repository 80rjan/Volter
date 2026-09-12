package com.volter.shop.modules.pawn.application.dto;

import com.volter.shop.modules.pawn.domain.model.enums.PawnNoteStatus;

import java.time.OffsetDateTime;

public record PawnNoteResponse(
        Long id,
        String description,
        PawnNoteStatus status,
        Long createdByStaffId,
        OffsetDateTime createdAt,
        OffsetDateTime resolvedAt
) {
}
