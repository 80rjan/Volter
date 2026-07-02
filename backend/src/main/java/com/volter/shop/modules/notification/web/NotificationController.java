package com.volter.shop.modules.notification.web;

import com.volter.shop.modules.notification.application.NotificationService;
import com.volter.shop.modules.notification.application.dto.NotificationDetailResponse;
import com.volter.shop.modules.notification.application.dto.NotificationDetailResult;
import com.volter.shop.modules.notification.application.dto.NotificationFilterRequest;
import com.volter.shop.modules.notification.application.dto.NotificationResponse;
import com.volter.shop.modules.notification.application.dto.UnreadCountResponse;
import com.volter.shop.modules.notification.infrastructure.mapper.NotificationMapper;
import com.volter.shared.security.StaffPrincipal;
import com.volter.shared.web.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated() and !hasRole('PRE_AUTH')")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    /**
     * Get all notifications, paginated, filtered by optional filters.
     */
    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponse>> list(@ModelAttribute NotificationFilterRequest filter,
                                                                   Pageable pageable,
                                                                   @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(PageResponse.of(notificationService.list(principal.staffId(), filter, pageable), notificationMapper::toResponse));
    }

    /**
     * Number of unread notifications for the current staff member (for the nav badge).
     */
    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> unreadCount(@AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(new UnreadCountResponse(notificationService.unreadCount(principal.staffId())));
    }

    /**
     * Detailed view of a notification, including a reference to the domain entity
     * it points at (PAWN contract / SALE) so the client can fetch its full details.
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<NotificationDetailResponse> detail(@PathVariable Long id,
                                                             @AuthenticationPrincipal StaffPrincipal principal) {
        NotificationDetailResult result = notificationService.detail(principal.staffId(), id);
        return ResponseEntity.ok(new NotificationDetailResponse(
                notificationMapper.toResponse(result.notification()), result.entityKind(), result.entityId()));
    }

    /**
     * Mark the notification as read.
     * Only the recipient of the notification can mark it as read.
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id,
                                                         @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(notificationMapper.toResponse(notificationService.markRead(principal.staffId(), id)));
    }

    /**
     * Mark all notifications as read for the current staff member.
     */
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal StaffPrincipal principal) {
        notificationService.markAllRead(principal.staffId());
        return ResponseEntity.noContent().build();
    }
}
