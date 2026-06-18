package com.volter.shop.modules.cashregister.domain.model;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionStatus;
import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * One open-to-close shift of a {@link CashRegister}, operated by a staff member
 * (identity context, by id). Balances are plain signed integers because a pawn
 * shop's drawer can legitimately go negative. At most one session per register
 * may be OPEN at a time (enforced by a partial unique index).
 */
@Entity
@Table(name = "cash_register_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CashRegisterSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Cash register is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cash_register_id", nullable = false, foreignKey = @ForeignKey(name = "fk_crs_cash_register"))
    private CashRegister cashRegister;

    @NotNull(message = "Staff is required")
    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @CreationTimestamp
    @Column(name = "opened_at", nullable = false)
    private OffsetDateTime openedAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @NotNull(message = "Opening balance is required")
    @Column(name = "opening_balance", nullable = false)
    private Integer openingBalance;

    @NotNull(message = "Current balance is required")
    @Column(name = "current_balance", nullable = false)
    private Integer currentBalance;

    @NotNull(message = "Expected interest is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "expected_interest", nullable = false))
    @Builder.Default
    private Money expectedInterest = new Money(0);

    @Column(name = "closing_balance")
    private Integer closingBalance;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private CashRegisterSessionStatus status = CashRegisterSessionStatus.OPEN;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;


    /**
     * Open a session.
     */
    public static CashRegisterSession open(CashRegister cashRegister, Long staffId, int openingBalance, Money expectedInterest) {
        return CashRegisterSession.builder()
                .cashRegister(cashRegister)
                .staffId(staffId)
                .openingBalance(openingBalance)
                .currentBalance(openingBalance)
                .expectedInterest(expectedInterest == null ? new Money(0) : expectedInterest)
                .build();
    }

    /**
     * Close the session.
     * Discrepancy is calculated as the diff between the staff-counted closing balance and the session's running current balance, which is the expected closing balance.
     */
    public CashRegisterSessionDiscrepancy close(int countedClosingBalance) {
        ensureOpen();
        this.closingBalance = countedClosingBalance;
        this.closedAt = OffsetDateTime.now();
        this.status = CashRegisterSessionStatus.CLOSED;

        // if positive, the drawer has more money than expected; if negative, less money
        int difference = countedClosingBalance - this.currentBalance;
        if (difference == 0) {
            return null;
        }

        return CashRegisterSessionDiscrepancy.of(this, this.currentBalance, countedClosingBalance, difference);
    }

    public void addExpectedInterest(Money interest) {
        ensureOpen();
        this.expectedInterest = this.expectedInterest.add(interest);
    }

    /**
     * Deposits money into the drawer (a transaction inflow). The running balance
     * is intentionally allowed to go negative.
     */
    public void deposit(Money amount) {
        ensureOpen();
        this.currentBalance += amount.amount();
    }

    /**
     * Withdraws money from the drawer (a transaction outflow). The running
     * balance is intentionally allowed to go negative.
     */
    public void withdraw(Money amount) {
        ensureOpen();
        this.currentBalance -= amount.amount();
    }

    public boolean isOpen() {
        return status == CashRegisterSessionStatus.OPEN;
    }

    private void ensureOpen() {
        if (status == CashRegisterSessionStatus.CLOSED) {
            throw new IllegalStateException("Operation not allowed on a CLOSED cash register session");
        }
    }
}
