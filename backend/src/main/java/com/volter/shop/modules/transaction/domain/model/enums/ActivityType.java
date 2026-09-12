package com.volter.shop.modules.transaction.domain.model.enums;

/**
 * The activity list's category. The first five mirror {@link TransactionType};
 * the rest are non-monetary events that have no ledger transaction.
 */
public enum ActivityType {
    PAWN,
    SALE,
    EXPENSE,
    CASH_REGISTER,
    STAFF_BONUS,
    /** A pawn contract lapsed and its item went up for sale. No money moved. */
    PAWN_FORFEITED
}
