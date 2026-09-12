package com.volter.shop.modules.pawn.application.dto;

import com.volter.shop.modules.pawn.domain.model.enums.PawnContractNoteStatus;
import jakarta.validation.constraints.NotNull;

/** Move a note between ACTIVE and RESOLVED. */
public record PawnContractNoteStatusUpdateRequest(
        @NotNull(message = "Status is required")
        PawnContractNoteStatus status
) {
}
