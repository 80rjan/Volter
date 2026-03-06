package com.volter.backend.modificationNotification;

import com.volter.backend.manager.Manager;
import com.volter.backend.modificationNotification.enums.ModificationSeverity;
import com.volter.backend.modificationNotification.enums.ModificationType;
import com.volter.backend.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_modification_notification_created_at", columnList = "createdAt DESC"),
                @Index(name = "idx_modification_notification_read_by_manager_created_at", columnList = "readByManager, createdAt DESC"),
                @Index(name = "idx_modification_notification_modification_type_created_at", columnList = "modificationType, createdAt DESC"),
                @Index(name = "idx_modification_notification_modification_severity_created_at", columnList = "modificationSeverity, createdAt DESC"),
        }
)
public class ModificationNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Modification type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModificationType modificationType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, ModificationChange> modifiedData;

    @NotNull(message = "Modification summary is required")
    @Column(nullable = false)
    private String summary;

    @NotNull(message = "Modification severity is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModificationSeverity modificationSeverity;

    @NotNull(message = "Modification created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Modification is read by manager is required")
    @Column(nullable = false)
    private boolean readByManager;

    @Column(nullable = true)
    private LocalDateTime readAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, foreignKey = @ForeignKey(name = "fk_modification_notification_transaction"))      // Transaction.id
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false, foreignKey = @ForeignKey(name = "fk_modification_notification_manager"))      // Manager.id
    private Manager manager;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        readByManager = false;
    }
}
