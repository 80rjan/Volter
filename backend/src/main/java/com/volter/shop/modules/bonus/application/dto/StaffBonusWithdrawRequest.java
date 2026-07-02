package com.volter.shop.modules.bonus.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Take part (or all) of the caller's available profit-share bonus as cash out of
 * an open cash register session.
 */
public record StaffBonusWithdrawRequest(
        @NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") Integer amount,
        @NotNull(message = "Cash register session id is required") Long cashRegisterSessionId
) {
}
