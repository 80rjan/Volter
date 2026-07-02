package com.volter.shop.modules.notification.application;

import com.volter.shop.modules.notification.application.dto.NotificationDetailResult;
import com.volter.shop.modules.notification.application.dto.NotificationFilterRequest;
import com.volter.shop.modules.notification.domain.model.Notification;
import com.volter.shop.modules.notification.domain.model.enums.NotificationType;
import com.volter.shop.modules.notification.domain.repository.NotificationRepository;
import com.volter.shop.modules.notification.domain.specification.NotificationSpecification;
import com.volter.shared.web.exception.ResourceNotFoundException;
import com.volter.shared.web.exception.UnauthorizedException;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.sale.domain.repository.SaleTransactionRepository;
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
    private final PawnTransactionRepository pawnTransactionRepository;
    private final PawnContractRepository pawnContractRepository;
    private final SaleTransactionRepository saleTransactionRepository;

    /**
     * Create and persist a notification for a staff member, optionally pointing
     * back to a domain entity via {@code entityType}/{@code entityId}. Other
     * modules call this to deliver alerts (e.g. a risk flag) to the recipient.
     */
    public Notification create(Long recipientStaffId, NotificationType type, String title,
                               String description, String entityType, Long entityId) {
        Notification notification = Notification.builder()
                .recipientStaffId(recipientStaffId)
                .type(type)
                .title(title)
                .description(description)
                .entityType(entityType)
                .entityId(entityId)
                .read(false)
                .build();
        return notificationRepository.save(notification);
    }

    /**
     * Lists notifications for a specific staff member, filtered by the provided criteria and paginated.
     */
    @Transactional(readOnly = true)
    public Page<Notification> list(Long staffId, NotificationFilterRequest filter, Pageable pageable) {
        return notificationRepository.findAll(NotificationSpecification.forRecipient(staffId, filter), pageable);
    }

    /** Number of unread notifications for the given staff member (drives the nav badge). */
    @Transactional(readOnly = true)
    public long unreadCount(Long staffId) {
        return notificationRepository.countByRecipientStaffIdAndReadFalse(staffId);
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

    /**
     * Detailed view of a notification: the notification plus a reference to the
     * domain entity it points at, resolved from {@code entityType}/{@code entityId}
     * (a pawn-transaction id -> its contract, a sale-transaction id -> its sale).
     * The linked entity lives in the caller's current shop schema. Read-only; use
     * {@link #markRead} to mark it read.
     */
    @Transactional(readOnly = true)
    public NotificationDetailResult detail(Long staffId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        if (!notification.getRecipientStaffId().equals(staffId)) {
            throw new UnauthorizedException("Notification does not belong to current staff");
        }

        String entityKind = null;
        Long entityId = null;
        if (notification.getEntityType() != null && notification.getEntityId() != null) {
            switch (notification.getEntityType()) {
                case "pawn_transaction" -> {
                    var pt = pawnTransactionRepository.findById(notification.getEntityId());
                    if (pt.isPresent()) {
                        entityKind = "PAWN";
                        entityId = pt.get().getPawnContract().getId();
                    }
                }
                case "sale_transaction" -> {
                    var st = saleTransactionRepository.findById(notification.getEntityId());
                    if (st.isPresent()) {
                        entityKind = "SALE";
                        entityId = st.get().getSale().getId();
                    }
                }
                case "pawn_contract" -> {
                    if (pawnContractRepository.existsById(notification.getEntityId())) {
                        entityKind = "PAWN";
                        entityId = notification.getEntityId();
                    }
                }
                default -> { /* unknown / unlinked entity type */ }
            }
        }
        return new NotificationDetailResult(notification, entityKind, entityId);
    }
}
