package com.volter.shop.modules.pawn.application.dto;

import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.model.event.PawnRedeemedEvent;

public record PawnRedemptionResult(
        PawnTransaction transaction,
        PawnRedeemedEvent event,
        boolean underpaid
) {
}
