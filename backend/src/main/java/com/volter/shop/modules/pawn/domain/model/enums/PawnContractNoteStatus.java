package com.volter.shop.modules.pawn.domain.model.enums;

/**
 * Lifecycle of a {@link com.volter.shop.modules.pawn.domain.model.PawnContractNote}.
 * A note starts ACTIVE and is moved to RESOLVED once it has been dealt with;
 * it can be moved back to ACTIVE.
 */
public enum PawnContractNoteStatus {
    ACTIVE,
    RESOLVED
}
