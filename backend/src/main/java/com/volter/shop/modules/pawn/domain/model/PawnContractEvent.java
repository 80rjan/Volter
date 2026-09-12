package com.volter.shop.modules.pawn.domain.model;

import com.volter.shop.modules.pawn.domain.model.enums.PawnContractEventAction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * A non-monetary thing that happened to a {@link PawnContract}. Forfeiture moves
 * no money, so it cannot be a {@link com.volter.shop.modules.transaction.domain.model.Transaction}
 * — those require a cash register session and shift its running balance. Staff
 * still need to see it happened, so it is recorded here and surfaced alongside
 * transactions on the activity list.
 */
@Entity
@Table(name = "pawn_contract_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PawnContractEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pawn contract is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pawn_contract_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pce_event_pawn_contract"))
    private PawnContract pawnContract;

    @NotNull(message = "Action is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private PawnContractEventAction action;

    @NotNull(message = "Staff is required")
    @Column(name = "performed_by_staff_id", nullable = false)
    private Long performedByStaffId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Occurred at is required")
    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    public static PawnContractEvent record(PawnContract pawnContract,
                                           PawnContractEventAction action,
                                           Long staffId,
                                           String description) {
        return PawnContractEvent.builder()
                .pawnContract(pawnContract)
                .action(action)
                .performedByStaffId(staffId)
                .description(description)
                .occurredAt(OffsetDateTime.now())
                .build();
    }
}
