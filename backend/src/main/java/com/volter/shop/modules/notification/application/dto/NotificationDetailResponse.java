package com.volter.shop.modules.notification.application.dto;

/**
 * Detailed view of a notification: the notification itself plus a reference to
 * the domain entity it points at. {@code entityKind} is "PAWN" or "SALE" (or
 * null when there is no linked entity), and {@code entityId} is the id the client
 * fetches from /pawns/{id} or /sales/{id} for the entity's full details.
 */
public record NotificationDetailResponse(
        NotificationResponse notification,
        String entityKind,
        Long entityId
) {
}
