package com.volter.shop.modules.pawn.application.dto;

import jakarta.validation.constraints.NotBlank;

/** A staff member adds a note to a pawn contract; the description is all they enter. */
public record PawnContractNoteCreateRequest(
        @NotBlank(message = "Description is required")
        String description
) {
}
