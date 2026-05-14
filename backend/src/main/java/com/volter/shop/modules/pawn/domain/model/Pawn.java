package com.volter.shop.modules.pawn.domain.model;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.pawn.web.request.PawnCreationRequest;
import com.volter.shop.modules.pawn.web.request.PawnModificationRequest;
import com.volter.shop.modules.pawn.application.dto.PawnModificationResult;
import com.volter.shop.modules.pawn.application.dto.PawnRedemptionResult;
import com.volter.shop.modules.pawn.application.dto.PawnRenewalResult;
import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import com.volter.shop.modules.pawn.domain.model.event.*;
import com.volter.shop.modules.pawn.domain.model.valueobject.MaturityDateChange;
import com.volter.shop.modules.pawn.domain.model.valueobject.PawnPeriod;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.pawn.domain.model.enums.PawnStatus;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionMarginType;
import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter // only for testing
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Table(
//        indexes = {
//                @Index(name = "idx_pawn_status", columnList = "status"),
//
//                @Index(name = "idx_pawn_active_maturity_date", columnList = "active, maturity_date"),
//                @Index(name = "idx_pawn_active_issue_date_desc", columnList = "active, issue_date DESC"),
//                @Index(name = "idx_pawn_status_maturity_date", columnList = "status, maturity_date"),
//                @Index(name = "idx_pawn_customer_id_active", columnList = "customer_id, active")
//        },
//        uniqueConstraints = {
//                @UniqueConstraint(name = "uk_pawn_item_active", columnNames = {"item_id", "active"})     // one active pawn per item
//        }
//)
public class Pawn {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Pawn amount is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false))
    private Money amount;

    @NotNull(message = "Pawn interest is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "interest", nullable = false))
    private Money interest;

    @NotNull(message = "Pawn period is required")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "issueDate", column = @Column(name = "issue_date", nullable = false)),
            @AttributeOverride(name = "maturityDate", column = @Column(name = "maturity_date", nullable = false))
    })
    private PawnPeriod period;

    @NotNull(message = "Pawn default duration in days is required")
    @Min(1)
    @Column(nullable = false)
    private Integer defaultDurationDays;

    @NotNull(message = "Pawn status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PawnStatus status = PawnStatus.ACTIVE;

    @NotNull(message = "Pawn active status is required")
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @NotNull(message = "Pawn customer is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_customer"))
    // Customer.id
    private Customer customer;

    @NotNull(message = "Pawn item is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_item"))
    // Item.id
    private Item item;

    @Builder.Default
    @OneToMany(mappedBy = "pawn", cascade = {CascadeType.PERSIST}, orphanRemoval = false, fetch = FetchType.LAZY)       // immutable, no update
    private List<PawnTransaction> transactions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "pawn", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<PawnEvent> pawnEvents = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Transient
    public PawnTransaction getInitialTransaction() {
        return transactions.stream()
                .filter(t -> t.getAction() == PawnTransactionAction.CREATION)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Initial creation transaction not found for pawn id: " + id));
    }


    public PawnRenewalResult renew(Money paidInterest, String transactionDescription, CashRegisterSession cashRegisterSession, Staff staff) {
        double pawnDailyInterest = this.interest.amount() * 1.0 / this.defaultDurationDays;
        int carryOverDays = Math.toIntExact(Math.round(paidInterest.amount() / pawnDailyInterest));      // days to extent the period.maturityDate

        LocalDate oldMaturityDate = this.period.maturityDate();
        this.period = this.period.extend(carryOverDays);

        PawnTransaction transaction = PawnTransaction.builder()
                .action(PawnTransactionAction.RENEWAL)
                .amount(paidInterest)
                .direction(TransactionDirection.IN)
                .marginAmount(paidInterest)
                .marginType(TransactionMarginType.PROFIT)
                .description(transactionDescription)
                .pawn(this)
                .staff(staff)
                .cashRegisterSession(cashRegisterSession)
                .build();
        this.transactions.add(transaction);

        PawnRenewedEvent pawnEvent = PawnRenewedEvent.builder()
                .maturityDateChange(new MaturityDateChange(oldMaturityDate, this.period.maturityDate()))
                .interestPaid(paidInterest)
                .pawn(this)
                .performedBy(staff)
                .build();
        this.pawnEvents.add(pawnEvent);

        return new PawnRenewalResult(transaction);
    }

    public PawnRedemptionResult redeem(Money paidAmount, String transactionDescription, CashRegisterSession cashRegisterSession, Staff staff) {
        this.status = PawnStatus.REDEEMED;
        this.active = false;

        boolean underpaid = paidAmount.isLessThan(this.amount.add(this.interest));      // for risk alert creation

        PawnTransaction transaction = PawnTransaction.builder()
                .action(PawnTransactionAction.REDEMPTION)
                .amount(paidAmount)
                .direction(TransactionDirection.IN)
                .marginAmount(paidAmount.absoluteSubtract(this.amount))
                .marginType(underpaid ? TransactionMarginType.LOSS : TransactionMarginType.PROFIT)
                .description(transactionDescription)
                .pawn(this)
                .staff(staff)
                .cashRegisterSession(cashRegisterSession)
                .build();
        this.transactions.add(transaction);

        PawnRedeemedEvent pawnEvent = PawnRedeemedEvent.builder()
                .totalAmountPaid(paidAmount)
                .interestPaid(paidAmount.absoluteSubtract(this.amount))
                .pawn(this)
                .performedBy(staff)
                .build();
        this.pawnEvents.add(pawnEvent);

        return new PawnRedemptionResult(transaction, pawnEvent, underpaid);
    }

    public void forfeit(String transactionDescription, CashRegisterSession cashRegisterSession, Staff staff) {
        this.status = PawnStatus.FORFEITED;
        this.active = false;

        PawnTransaction transaction = PawnTransaction.builder()
                .action(PawnTransactionAction.FORFEITURE)
                .amount(new Money(0))
                .direction(TransactionDirection.NEUTRAL)        // no money changed, item goes to sale
                .marginAmount(new Money(0))
                .marginType(TransactionMarginType.NEUTRAL)
                .description(transactionDescription)
                .pawn(this)
                .staff(staff)
                .cashRegisterSession(cashRegisterSession)
                .build();
        this.transactions.add(transaction);

        PawnForfeitedEvent pawnEvent = PawnForfeitedEvent.builder()
                .maturityDate(this.period.maturityDate())
                .unpaidAmount(this.amount)
                .unpaidInterest(this.interest)
                .pawn(this)
                .performedBy(staff)
                .build();
        this.pawnEvents.add(pawnEvent);
    }

    public PawnModificationResult modify(PawnModificationRequest request, CashRegisterSession cashRegisterSession, Staff staff) {
        int oldAmount = this.amount.amount();
        int oldInterest = this.interest.amount();
        int oldDefaultDurationDays = this.defaultDurationDays;

        int amountDifference = 0;
        if (request.amount() != null) {
            amountDifference = request.amount() - this.amount.amount();
            this.amount = new Money(request.amount());
        }
        if (request.interest() != null) {
            this.interest = new Money(request.interest());
        }
        if (request.durationDays() != null) {
            this.defaultDurationDays = request.durationDays();
        }

        PawnTransaction transaction = PawnTransaction.builder()
                .action(PawnTransactionAction.MODIFICATION)
                .amount(new Money(Math.abs(amountDifference)))
                .direction(amountDifference == 0 ? TransactionDirection.NEUTRAL : amountDifference > 0 ? TransactionDirection.OUT : TransactionDirection.IN)
                .marginAmount(new Money(0))       // no profit/loss, just correction
                .marginType(TransactionMarginType.NEUTRAL)
                .description(request.transactionDescription())
                .pawn(this)
                .staff(staff)
                .cashRegisterSession(cashRegisterSession)
                .build();
        this.transactions.add(transaction);

        PawnModifiedEvent pawnEvent = PawnModifiedEvent.builder()
                .previousSnapshot(PawnSnapshot.builder()
                        .amount(oldAmount)
                        .interest(oldInterest)
                        .defaultDurationDays(oldDefaultDurationDays)
                        .build())
                .newSnapshot(PawnSnapshot.builder()
                        .amount(this.amount.amount())
                        .interest(this.interest.amount())
                        .defaultDurationDays(this.defaultDurationDays)
                        .build())
                .pawn(this)
                .performedBy(staff)
                .build();
        this.pawnEvents.add(pawnEvent);

        return new PawnModificationResult(transaction, pawnEvent, amountDifference);
    }

    public static Pawn create(PawnCreationRequest request, CashRegisterSession cashRegisterSession, Staff staff, Item item, Customer customer) {
        Pawn pawn = Pawn.builder()
                .amount(new Money(request.getAmount()))
                .interest(new Money(request.getInterest()))
                .period(new PawnPeriod(request.getIssueDate(), request.getMaturityDate()))
                .defaultDurationDays(request.getDurationDays())
                .item(item)
                .customer(customer)
                .build();

        PawnTransaction transaction = PawnTransaction.builder()
                .action(PawnTransactionAction.CREATION)
                .amount(new Money(request.getAmount()))
                .direction(TransactionDirection.OUT)
                .marginAmount(new Money(0))     // no profit/loss on creation, just initial loan
                .marginType(TransactionMarginType.NEUTRAL)
                .description(request.getTransactionDescription())
                .pawn(pawn)
                .staff(staff)
                .cashRegisterSession(cashRegisterSession)
                .build();
        pawn.transactions.add(transaction);

        PawnCreatedEvent pawnEvent = PawnCreatedEvent.builder()
                .pawn(pawn)
                .performedBy(staff)
                .build();
        pawn.pawnEvents.add(pawnEvent);

        return pawn;
    }
}
