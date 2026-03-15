package com.volter.backend.notification;

import com.volter.backend.notification.enums.NotificationSeverity;
import com.volter.backend.notification.enums.NotificationType;
import com.volter.backend.pawnEvent.PawnEvent;
import com.volter.backend.staff.Staff;
import com.volter.backend.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        indexes = {
                @Index(name = "idx_notification_created_at", columnList = "createdAt DESC"),
                @Index(name = "idx_notification_read_by_manager_created_at", columnList = "readByManager, createdAt DESC"),
                @Index(name = "idx_notification_notification_type_created_at", columnList = "notificationType, createdAt DESC"),
                @Index(name = "idx_notification_notification_severity_created_at", columnList = "notificationSeverity, createdAt DESC"),
        }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Notification type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @NotNull(message = "Notification severity is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationSeverity severity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @NotNull(message = "Notification summary is required")
    @Column(nullable = false)
    private String summary;

    @NotNull(message = "Notification created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Notification is read by manager is required")
    @Column(nullable = false)
    @Builder.Default
    private boolean readByManager = false;

    @Column(nullable = true)
    private LocalDateTime readAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = true, foreignKey = @ForeignKey(name = "fk_notification_transaction"))      // Transaction.id
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_manager"))      // Staff.id
    private Staff manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pawn_event_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notification_pawn_event"))      // PawnEvent.id
    private PawnEvent pawnEvent;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
