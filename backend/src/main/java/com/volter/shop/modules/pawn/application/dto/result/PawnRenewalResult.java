package com.volter.shop.modules.pawn.application.dto.result;

import com.volter.shop.modules.pawn.domain.model.PawnTransaction;

public record PawnRenewalResult(
        PawnTransaction transaction
) {
}
