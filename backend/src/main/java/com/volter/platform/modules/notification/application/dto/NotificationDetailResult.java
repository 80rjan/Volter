package com.volter.platform.modules.notification.application.dto;

import com.volter.platform.modules.notification.domain.model.Notification;

/**
 * A notification together with the resolved reference to the domain entity it is
 * about: {@code entityKind} ("PAWN"/"SALE"/null) and the id of that entity
 * (a pawn contract id or sale id) the client can fetch for full details.
 */
public record NotificationDetailResult(
        Notification notification,
        String entityKind,
        Long entityId
) {
}
