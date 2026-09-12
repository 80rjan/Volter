package com.volter.shop.modules.pawn.application.dto;

import com.volter.shop.modules.pawn.domain.model.enums.PawnNoteStatus;
import jakarta.validation.constraints.NotNull;

/** Move a note between ACTIVE and RESOLVED. */
public record PawnNoteStatusUpdateRequest(
        @NotNull(message = "Status is required")
        PawnNoteStatus status
) {
}
