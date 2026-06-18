package com.volter.platform.modules.notification.application;

import com.volter.platform.modules.notification.application.dto.NotificationFilterRequest;
import com.volter.platform.modules.notification.domain.model.Notification;
import com.volter.platform.modules.notification.domain.repository.NotificationRepository;
import com.volter.platform.modules.notification.domain.specification.NotificationSpecification;
import com.volter.shared.web.exception.ResourceNotFoundException;
import com.volter.shared.web.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Lists notifications for a specific staff member, filtered by the provided criteria and paginated.
     */
    @Transactional(readOnly = true)
    public Page<Notification> list(Long staffId, NotificationFilterRequest filter, Pageable pageable) {
        return notificationRepository.findAll(NotificationSpecification.forRecipient(staffId, filter), pageable);
    }

    /**
     * Mark the notification as read.
     * If the notification does not belong to the current staff, an UnauthorizedException is thrown.
     */
    public Notification markRead(Long staffId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        if (!notification.getRecipientStaffId().equals(staffId)) {
            throw new UnauthorizedException("Notification does not belong to current staff");
        }
        notification.markAsRead();
        return notification;
    }

    /**
     * Mark all notifications as read for the current staff member.
     */
    public int markAllRead(Long staffId) {
        return notificationRepository.markAllAsRead(staffId, OffsetDateTime.now());
    }
}
