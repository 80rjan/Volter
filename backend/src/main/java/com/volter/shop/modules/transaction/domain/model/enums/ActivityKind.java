package com.volter.shop.modules.transaction.domain.model.enums;

/** Which source an activity row came from. */
public enum ActivityKind {
    /** A ledger transaction: money moved, amount and direction are set. */
    TRANSACTION,
    /** A pawn contract event: no money moved, amount and direction are null. */
    PAWN_EVENT
}
