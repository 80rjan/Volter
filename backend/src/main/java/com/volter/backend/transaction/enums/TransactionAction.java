package com.volter.backend.transaction.enums;

public enum TransactionAction {
    // General
    CREATION,
    MODIFICATION,

    // Pawn specific
    RENEWAL,
    REDEMPTION,
    FORFEITURE,

    // Sale specific
    PURCHASE,
    SALE,

    // Expense specific
    PAYMENT,

    // Cash specific
    WITHDRAW,
    DEPOSIT,
}
