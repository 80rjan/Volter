package com.volter.shop.modules.alert.domain;

import com.volter.shop.modules.alert.web.request.RiskAlertCreationRequest;
import com.volter.shop.modules.alert.domain.enums.RiskAlertSeverity;
import com.volter.shop.modules.alert.domain.enums.RiskAlertType;
import com.volter.shop.modules.pawn.domain.model.event.PawnEvent;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.transaction.domain.model.Transaction;
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
//@Table(
//        indexes = {
//                @Index(name = "idx_risk_alert_created_at", columnList = "created_at DESC"),
//                @Index(name = "idx_risk_alert_read_by_manager_created_at", columnList = "read_by_manager, created_at DESC"),
//                @Index(name = "idx_risk_alert_type_created_at", columnList = "type, created_at DESC"),
//                @Index(name = "idx_risk_alert_severity_created_at", columnList = "severity, created_at DESC"),
//        }
//)
public class RiskAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "RiskAlert type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskAlertType type;

    @NotNull(message = "RiskAlert severity is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskAlertSeverity severity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @NotNull(message = "RiskAlert summary is required")
    @Column(nullable = false)
    private String summary;

    @NotNull(message = "RiskAlert created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "RiskAlert is read by manager is required")
    @Column(nullable = false)
    @Builder.Default
    private boolean readByManager = false;

    @Column(nullable = true)
    private LocalDateTime readAt;

    @NotNull(message = "RiskAlert transaction is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, foreignKey = @ForeignKey(name = "fk_risk_alert_transaction"))      // Transaction.id
    private Transaction transaction;

    @NotNull(message = "RiskAlert manager is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false, foreignKey = @ForeignKey(name = "fk_risk_alert_manager"))      // Staff.id
    private Staff manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pawn_event_id", nullable = true, foreignKey = @ForeignKey(name = "fk_risk_alert_pawn_event"))      // PawnEvent.id
    private PawnEvent pawnEvent;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public static RiskAlert create(RiskAlertCreationRequest request, Transaction transaction, Staff manager, PawnEvent pawnEvent) {
        return RiskAlert.builder()
                .type(request.type())
                .severity(request.severity())
                .metadata(request.metadata())
                .summary(request.summary())
                .transaction(transaction)
                .manager(manager)
                .pawnEvent(pawnEvent)
                .build();
    }

    public void markAsRead() {
        this.readByManager = true;
        this.readAt = LocalDateTime.now();
    }
}
