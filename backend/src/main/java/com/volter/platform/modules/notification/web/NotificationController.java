package com.volter.platform.modules.notification.web;

import com.volter.platform.modules.notification.application.NotificationService;
import com.volter.platform.modules.notification.application.dto.NotificationFilterRequest;
import com.volter.platform.modules.notification.application.dto.NotificationResponse;
import com.volter.platform.modules.notification.application.dto.UnreadCountResponse;
import com.volter.platform.modules.notification.infrastructure.mapper.NotificationMapper;
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
