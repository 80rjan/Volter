package com.volter.shop.modules.pawn.domain.model.enums;

/**
 * A thing that happened to a pawn contract without moving money. Monetary
 * actions (creation, redemption, extension) are ledger transactions instead —
 * see {@link PawnTransactionAction}.
 */
public enum PawnContractEventAction {
    /** The contract lapsed and the item went up for sale. No payment involved. */
    FORFEITED
}
