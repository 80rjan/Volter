package com.volter.shop.modules.pawn.domain.model;

import com.volter.shop.modules.pawn.domain.model.enums.PawnNoteStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * A free-form note a staff member attaches to a {@link PawnContract} — a
 * reminder, a call with the client, something about the item. It carries only a
 * description; a contract may hold any number of them.
 *
 * <p>A note is ACTIVE until someone marks it RESOLVED, and may be moved back to
 * ACTIVE. Notes are independent of the contract's own status: a redeemed or
 * forfeited contract can still be annotated.
 */
@Entity
@Table(name = "pawn_note")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PawnNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pawn contract is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pawn_contract_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_note_pawn_contract"))
    private PawnContract pawnContract;

    @NotNull(message = "Staff is required")
    @Column(name = "created_by_staff_id", nullable = false)
    private Long createdByStaffId;

    @NotBlank(message = "Description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PawnNoteStatus status = PawnNoteStatus.ACTIVE;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public static PawnNote of(PawnContract pawnContract, Long staffId, String description) {
        return PawnNote.builder()
                .pawnContract(pawnContract)
                .createdByStaffId(staffId)
                .description(description)
                .status(PawnNoteStatus.ACTIVE)
                .build();
    }

    /** Mark as dealt with. Re-resolving an already resolved note keeps the original timestamp. */
    public void resolve() {
        if (status == PawnNoteStatus.RESOLVED) {
            return;
        }
        this.status = PawnNoteStatus.RESOLVED;
        this.resolvedAt = OffsetDateTime.now();
    }

    /** Move back to ACTIVE, clearing the resolution timestamp. */
    public void reopen() {
        this.status = PawnNoteStatus.ACTIVE;
        this.resolvedAt = null;
    }

    public boolean isActive() {
        return status == PawnNoteStatus.ACTIVE;
    }
}
