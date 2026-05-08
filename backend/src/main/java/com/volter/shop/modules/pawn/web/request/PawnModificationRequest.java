package com.volter.shop.modules.pawn.web.request;

import com.volter.shop.modules.inventory.web.request.baseitem.ItemModificationRequest;

public record PawnModificationRequest (
    Integer amount,
    Integer interest,
    Integer durationDays,
    ItemModificationRequest itemModificationRequest,

    String transactionDescription
) {}
