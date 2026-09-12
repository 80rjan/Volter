package com.volter.shop.modules.transaction.application.dto;

import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.ActivityKind;
import com.volter.shop.modules.transaction.domain.model.enums.ActivityType;

import java.time.OffsetDateTime;

/**
 * One row of the activity list. A TRANSACTION row is a ledger entry with an
 * amount, direction and session; a PAWN_EVENT row is something that happened
 * without money moving, so those three are null.
 *
 * <p>{@code id} is the id within the row's own source ({@code kind}) — use both
 * to address it. {@code entryId} is unique across the whole list.
 */
public record TransactionResponse(
        String entryId,
        ActivityKind kind,
        Long id,
        Long staffId,
        Long cashRegisterSessionId,
        ActivityType type,
        Integer amount,
        TransactionDirection direction,
        String description,
        String clientName,
        OffsetDateTime createdAt
) {
}
