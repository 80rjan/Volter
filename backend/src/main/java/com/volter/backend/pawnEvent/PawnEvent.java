package com.volter.backend.pawnEvent;

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

    @NotNull(message = "Pawn event pawn snapshot is required")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private PawnSnapshot pawnSnapshot;

    @NotNull(message = "Pawn event changes snapshot is required")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private PawnChangesSnapshot changesSnapshot;

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

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
