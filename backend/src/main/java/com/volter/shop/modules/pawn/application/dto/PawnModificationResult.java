package com.volter.shop.modules.pawn.application.dto;

import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.model.event.PawnModifiedEvent;

public record PawnModificationResult(
        PawnTransaction transaction,
        PawnModifiedEvent event,
        Integer amountDifference
) {
}
