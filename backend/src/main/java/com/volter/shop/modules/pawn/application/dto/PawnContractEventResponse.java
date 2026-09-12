package com.volter.shop.modules.pawn.application.dto;

import com.volter.shop.modules.pawn.domain.model.enums.PawnContractEventAction;

import java.time.OffsetDateTime;

/** A non-monetary pawn contract event, as read by the transaction module's activity list. */
public record PawnContractEventResponse(
        Long id,
        Long pawnContractId,
        PawnContractEventAction action,
        Long performedByStaffId,
        String description,
        OffsetDateTime occurredAt
) {
}
