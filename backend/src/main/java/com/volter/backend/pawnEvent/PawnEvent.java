package com.volter.backend.pawnEvent;

import com.volter.backend.notification.Notification;
import com.volter.backend.pawn.Pawn;
import com.volter.backend.pawnEvent.enums.PawnEventType;
import com.volter.backend.pawnEvent.snapshot.PawnChangesSnapshot;
import com.volter.backend.pawnEvent.snapshot.PawnSnapshot;
import com.volter.backend.staff.Staff;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class PawnEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Pawn event type is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PawnEventType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private PawnSnapshot pawnSnapshot;              // only for modification event, otherwise null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private PawnChangesSnapshot changesSnapshot;    // only for modification events, otherwise null

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
    private Staff staff;

    @Builder.Default
    @OneToMany(mappedBy = "pawnEvent", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Notification> notifications = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
