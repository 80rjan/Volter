package com.volter.shop.modules.cashregister.domain.model.enums;

/**
 * Lifecycle state of a {@link com.volter.shop.modules.cashregister.domain.model.CashRegister}.
 * Registers are retired by moving to {@code INACTIVE} (never deleted, so their
 * historical sessions/transactions stay intact); only {@code ACTIVE} ones are listed.
 */
public enum CashRegisterStatus {
    ACTIVE,
    INACTIVE
}
