package com.volter.shop.modules.transaction.application.dto;

import com.volter.identity.modules.staff.application.dto.StaffResponse;
import com.volter.shop.modules.cashregister.application.dto.CashRegisterSessionResponse;
import com.volter.shop.modules.expense.application.dto.ExpenseResponse;
import com.volter.shop.modules.pawn.application.dto.PawnContractDetailedResponse;
import com.volter.shop.modules.sale.application.dto.SaleDetailedResponse;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.ActivityType;

import java.time.OffsetDateTime;

/**
 * Full view of a single activity entry: its ledger data, the staff member who
 * made it, and exactly one type-specific detail block matching {@link #type}.
 * The other three detail blocks are {@code null}. For a non-monetary event
 * {@code amount}, {@code direction} and {@code cashRegisterSessionId} are null
 * too, and the pawn block carries the contract it happened to.
 */
public record TransactionDetailedResponse(
        Long id,
        ActivityType type,
        Integer amount,
        TransactionDirection direction,
        String description,
        OffsetDateTime createdAt,
        Long cashRegisterSessionId,
        StaffResponse staff,
        PawnContractDetailedResponse pawn,
        SaleDetailedResponse sale,
        ExpenseResponse expense,
        CashRegisterSessionResponse cashRegisterSession
) {
}
