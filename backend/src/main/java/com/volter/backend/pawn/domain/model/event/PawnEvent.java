package com.volter.backend.pawn.domain.model.event;

import com.volter.backend.notification.Notification;
import com.volter.backend.pawn.domain.model.Pawn;
import com.volter.backend.pawn.domain.model.enums.PawnEventType;
import com.volter.backend.staff.Staff;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @NotNull(message = "Pawn event note is required")
    @Column(nullable = false)
    private String note;

    @NotNull(message = "Pawn event created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pawn_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_event_pawn"))
    private Pawn pawn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_event_staff"))
    private Staff performedBy;

    @Builder.Default
    @OneToMany(mappedBy = "pawnEvent", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Notification> notifications = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
