package com.volter.shop.modules.cashregister.domain.model;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyPhase;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyStatus;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * A cash mismatch detected when a {@link CashRegisterSession} is closed:
 * counted balance vs. expected balance. {@code difference} is signed
 * (positive = overage, negative = shortage). Stays OPEN until a manager
 * resolves it.
 */
@Entity
@Table(name = "cash_register_session_discrepancy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CashRegisterSessionDiscrepancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Session is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cash_register_session_id", nullable = false, foreignKey = @ForeignKey(name = "fk_crsd_session"))
    private CashRegisterSession session;

    @Column(name = "resolved_by_staff_id")
    private Long resolvedByStaffId;

    @NotNull(message = "Expected amount is required")
    @Column(name = "expected_amount", nullable = false)
    private Integer expectedAmount;

    @NotNull(message = "Counted amount is required")
    @Column(name = "counted_amount", nullable = false)
    private Integer countedAmount;

    @NotNull(message = "Difference is required")
    @Column(name = "difference", nullable = false)
    private Integer difference;

    @NotNull(message = "Discrepancy type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CashRegisterSessionDiscrepancyType type;

    @NotNull(message = "Discrepancy phase is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "phase", nullable = false)
    @Builder.Default
    private CashRegisterSessionDiscrepancyPhase phase = CashRegisterSessionDiscrepancyPhase.CLOSING;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private CashRegisterSessionDiscrepancyStatus status = CashRegisterSessionDiscrepancyStatus.OPEN;

    @Column(name = "resolution_note", columnDefinition = "TEXT")
    private String resolutionNote;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;


    static CashRegisterSessionDiscrepancy of(CashRegisterSession session, int expectedAmount, int countedAmount, int difference) {
        return CashRegisterSessionDiscrepancy.builder()
                .session(session)
                .expectedAmount(expectedAmount)
                .countedAmount(countedAmount)
                .difference(difference)
                .type(difference > 0 ? CashRegisterSessionDiscrepancyType.OVERAGE : CashRegisterSessionDiscrepancyType.SHORTAGE)
                .phase(CashRegisterSessionDiscrepancyPhase.CLOSING)
                .build();
    }

    /**
     * An OPENING discrepancy: the new session's opening balance differs from the
     * previous session's counted closing balance (cash changed while closed).
     */
    public static CashRegisterSessionDiscrepancy ofOpening(CashRegisterSession session, int previousClosingBalance, int newOpeningBalance) {
        int difference = newOpeningBalance - previousClosingBalance;
        return CashRegisterSessionDiscrepancy.builder()
                .session(session)
                .expectedAmount(previousClosingBalance)
                .countedAmount(newOpeningBalance)
                .difference(difference)
                .type(difference > 0 ? CashRegisterSessionDiscrepancyType.OVERAGE : CashRegisterSessionDiscrepancyType.SHORTAGE)
                .phase(CashRegisterSessionDiscrepancyPhase.OPENING)
                .build();
    }

    public void resolve(Long resolvedByStaffId, String resolutionNote) {
        if (status == CashRegisterSessionDiscrepancyStatus.RESOLVED) {
            return;
        }
        this.status = CashRegisterSessionDiscrepancyStatus.RESOLVED;
        this.resolvedByStaffId = resolvedByStaffId;
        this.resolutionNote = resolutionNote;
        this.resolvedAt = OffsetDateTime.now();
    }

    public boolean isResolved() {
        return status == CashRegisterSessionDiscrepancyStatus.RESOLVED;
    }
}
