package com.volter.platform.modules.notification.domain.model;

import com.volter.platform.modules.notification.domain.model.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * A message delivered to a staff member (e.g. a risk flag or a cash register
 * discrepancy). May optionally point back to a domain entity via
 * {@code entityType}/{@code entityId}.
 */
@Entity
@Table(schema = "public", name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Recipient staff is required")
    @Column(name = "recipient_staff_id", nullable = false)
    private Long recipientStaffId;

    @Column(name = "shop_id")
    private Long shopId;

    @NotNull(message = "Notification type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @NotBlank(message = "Title is required")
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "entity_type", length = 100)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @NotNull
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(name = "read_at")
    private OffsetDateTime readAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public void markAsRead() {
        if (!read) {
            this.read = true;
            this.readAt = OffsetDateTime.now();
        }
    }

    public boolean references(String entityType, Long entityId) {
        return entityType.equals(this.entityType) && entityId.equals(this.entityId);
    }
}
