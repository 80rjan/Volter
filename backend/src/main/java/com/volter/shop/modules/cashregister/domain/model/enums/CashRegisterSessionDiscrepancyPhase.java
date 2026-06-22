package com.volter.shop.modules.cashregister.domain.model.enums;

/**
 * When a cash discrepancy was detected:
 * - CLOSING: counted closing balance differs from the system's expected balance.
 * - OPENING: the new opening balance differs from the previous session's counted
 *   closing balance (cash changed while the drawer was closed).
 */
public enum CashRegisterSessionDiscrepancyPhase {
    OPENING,
    CLOSING
}
