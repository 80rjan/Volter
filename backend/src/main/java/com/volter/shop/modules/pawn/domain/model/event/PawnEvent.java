package com.volter.shop.modules.pawn.domain.model.event;

import com.volter.shop.modules.alert.domain.model.RiskAlert;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.model.enums.PawnEventType;
import com.volter.shop.modules.staff.domain.model.Staff;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type", discriminatorType = DiscriminatorType.STRING)
public abstract class PawnEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", insertable = false, updatable = false)
    private PawnEventType eventType;

    @Column(nullable = true)
    private String note;

    @NotNull(message = "Pawn event created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pawn_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_event_pawn"))
    private Pawn pawn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_event_staff"))
    private Staff performedBy;      // This is for simpler queries. Could be derived from transaction, but that would require a join to get the staff for an event. Also with this i can have pawn events that are not associated with a transaction.

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = true, foreignKey = @ForeignKey(name = "fk_pawn_event_transaction"))
    private PawnTransaction transaction;

    @Builder.Default
    @OneToMany(mappedBy = "pawnEvent", orphanRemoval = false)
    private List<RiskAlert> riskAlerts = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
