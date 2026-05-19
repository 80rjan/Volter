package com.volter.shop.modules.cashregister.domain.model;

import com.volter.shop.modules.cashregister.application.dto.CashRegisterSessionCloseResult;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyType;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionStatus;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterTransactionAction;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionMarginType;
import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
// todo: add check for when status = CLOSED, the nullables must not be null
public class CashRegisterSession {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Cash register session status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CashRegisterSessionStatus status = CashRegisterSessionStatus.OPEN;

    @NotNull(message = "Cash register session opened at is required")
    @Column(nullable = false)
    private LocalDateTime openedAt;

    @NotNull(message = "Cash register session updated at is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime closedAt;

    @NotNull(message = "Cash register session opening balance is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "opening_balance", nullable = false))
    private Money openingBalance;   // could be a problem because pawn shops wants negative balance

    @NotNull(message = "Cash register session current balance is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "current_balance", nullable = false))
    private Money currentBalance;   // could be a problem because pawn shops wants negative balance

    @NotNull(message = "Cash register session expected pawn interest is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "expected_pawn_interest", nullable = false))
    private Money expectedPawnInterest;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "closing_balance", nullable = true))
    private Money closingBalance;   // could be a problem because pawn shops wants negative balance

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "discrepancy", nullable = true))
    private Money discrepancy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private CashRegisterSessionDiscrepancyType discrepancyType;

    @Builder.Default
    @OneToMany(mappedBy = "cashRegisterSession", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<Transaction> transactions = new ArrayList<>();     // todo: transactions aggregate? no transaction cash reg

    @NotNull(message = "Cash register session cash register is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cash_register_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cash_register_session_cash_register"))
    private CashRegister cashRegister;

    @OneToOne(mappedBy = "cashRegisterSession", cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
    private CashRegisterSessionAdjustment adjustment;

    @NotNull(message = "Cash register session staff is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cash_register_session_staff"))
    private Staff staff;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.openedAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public static CashRegisterSession create(Money openingBalance, CashRegister cashRegister, Staff staff, List<Pawn> maturedPawns) {
        int expectedPawnInterest = maturedPawns.stream()
                .mapToInt(t -> t.getInterest().amount())
                .sum();

        CashRegisterSession session = CashRegisterSession.builder()
                .cashRegister(cashRegister)
                .staff(staff)
                .openingBalance(openingBalance)
                .currentBalance(openingBalance)
                .expectedPawnInterest(new Money(expectedPawnInterest))
                .build();

        CashRegisterTransaction cashRegisterTransaction = CashRegisterTransaction.builder()
                .amount(new Money(0))
                .direction(TransactionDirection.NEUTRAL)
                .marginAmount(new Money(0))
                .marginType(TransactionMarginType.NEUTRAL)
                .cashRegisterSession(session)
                .staff(staff)
                .action(CashRegisterTransactionAction.OPEN_SESSION)
                .build();
        session.transactions.add(cashRegisterTransaction);

        return session;
    }

    public CashRegisterSessionCloseResult close(Money closingBalance, Staff staff) {
        if (this.status == CashRegisterSessionStatus.CLOSED) {
            throw new IllegalStateException("Operation not allowed when cash register session is CLOSED");
        }

        this.staff = staff;
        this.closedAt = LocalDateTime.now();
        this.closingBalance = closingBalance;
        this.discrepancy = closingBalance.absoluteSubtract(this.currentBalance);
        int discrepancySigned = closingBalance.amount() - this.currentBalance.amount();
        this.discrepancyType = discrepancySigned > 0 ? CashRegisterSessionDiscrepancyType.OVERAGE :
                (discrepancySigned < 0 ? CashRegisterSessionDiscrepancyType.SHORTAGE : CashRegisterSessionDiscrepancyType.NONE);
        this.status = CashRegisterSessionStatus.CLOSED;

        CashRegisterTransaction transaction = CashRegisterTransaction.builder()
                .amount(new Money(0))
                .direction(TransactionDirection.NEUTRAL)
                .marginAmount(new Money(0))
                .marginType(TransactionMarginType.NEUTRAL)
                .cashRegisterSession(this)
                .staff(this.staff)
                .action(CashRegisterTransactionAction.CLOSE_SESSION)
                .cashRegisterSession(this)
                .build();
        this.transactions.add(transaction);

        return new CashRegisterSessionCloseResult(this.discrepancy, this.discrepancyType, transaction);    // return discrepancy and its direction to know if there is a cash shortage or overage in the session and create a alert
    }

    public void withdraw(Money amount, String transactionDescription) {
        if (this.status == CashRegisterSessionStatus.CLOSED) {
            throw new IllegalStateException("Operation not allowed when cash register session is CLOSED");
        }

        this.currentBalance = this.currentBalance.subtract(amount);

        CashRegisterTransaction transaction = CashRegisterTransaction.builder()
                .amount(new Money(amount.amount()))
                .direction(TransactionDirection.OUT)
                .marginAmount(new Money(0))
                .marginType(TransactionMarginType.NEUTRAL)
                .cashRegisterSession(this)
                .staff(this.staff)
                .action(CashRegisterTransactionAction.WITHDRAW)
                .description(transactionDescription)
                .cashRegisterSession(this)
                .build();
        this.transactions.add(transaction);
    }

    public void deposit(Money amount, String transactionDescription) {
        if (this.status == CashRegisterSessionStatus.CLOSED) {
            throw new IllegalStateException("Operation not allowed when cash register session is CLOSED");
        }

        this.currentBalance = this.currentBalance.add(amount);

        CashRegisterTransaction transaction = CashRegisterTransaction.builder()
                .amount(new Money(amount.amount()))
                .direction(TransactionDirection.IN)
                .marginAmount(new Money(0))
                .marginType(TransactionMarginType.NEUTRAL)
                .cashRegisterSession(this)
                .staff(this.staff)
                .action(CashRegisterTransactionAction.DEPOSIT)
                .description(transactionDescription)
                .cashRegisterSession(this)
                .build();
        this.transactions.add(transaction);
    }

    public void recordTransaction(Transaction transaction) {
        if (this.status == CashRegisterSessionStatus.CLOSED) {
            throw new IllegalStateException("Operation not allowed when cash register session is CLOSED");
        }

        Money amount = transaction.getAmount();
        if (transaction.getDirection() == TransactionDirection.IN)
            this.currentBalance = this.currentBalance.add(amount);
        else if (transaction.getDirection() == TransactionDirection.OUT)
            this.currentBalance = this.currentBalance.subtract(amount);
//        else unchanged
    }
}
