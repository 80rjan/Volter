package com.volter.shop.modules.pawn.domain.model;

import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.pawn.domain.model.enums.PawnContractStatus;
import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The aggregate root for a loan secured by a pawned {@link Item}. Tracks the
 * principal, interest and due date, and transitions through ACTIVE → REDEEMED
 * or FORFEITED. Extensions push the due date out and are recorded as
 * {@link PawnContractExtension}s. At most one ACTIVE contract may exist per item
 * (enforced by a partial unique index).
 */
@Entity
@Table(name = "pawn_contract")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PawnContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_contract_customer"))
    private Customer customer;

    @NotNull(message = "Item is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pawn_contract_item"))
    private Item item;

    @NotNull(message = "Staff is required")
    @Column(name = "created_by_staff_id", nullable = false)
    private Long createdByStaffId;

    @NotNull(message = "Principal amount is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "principal_amount", nullable = false))
    private Money principalAmount;

    @NotNull(message = "Interest amount is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "interest_amount", nullable = false))
    private Money interestAmount;

    @NotNull(message = "Term in days is required")
    @Min(1)
    @Column(name = "term_days", nullable = false)
    private Integer termDays;

    @NotNull(message = "Issue date is required")
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull(message = "Original due date is required")
    @Column(name = "original_due_date", nullable = false)
    private LocalDate originalDueDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PawnContractStatus status = PawnContractStatus.ACTIVE;

    @Column(name = "redeemed_at")
    private OffsetDateTime redeemedAt;

    @Column(name = "forfeited_at")
    private OffsetDateTime forfeitedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Builder.Default
    @OneToMany(mappedBy = "pawnContract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PawnContractExtension> extensions = new ArrayList<>();


    public static PawnContract create(Customer customer,
                                      Item item,
                                      Long staffId,
                                      Money principalAmount,
                                      Money interestAmount,
                                      int termDays,
                                      LocalDate issueDate) {
        LocalDate dueDate = issueDate.plusDays(termDays);
        return PawnContract.builder()
                .customer(customer)
                .item(item)
                .createdByStaffId(staffId)
                .principalAmount(principalAmount)
                .interestAmount(interestAmount)
                .termDays(termDays)
                .issueDate(issueDate)
                .dueDate(dueDate)
                .originalDueDate(dueDate)
                .status(PawnContractStatus.ACTIVE)
                .build();
    }

    /**
     * Extend the contract due date based on the interest paid.
     * Create a new PawnContractExtension record to track the extension details.
     */
    // FUTURE: Implement fee calculation logic when fee support is introduced.
    public PawnContractExtension extend(Money interestPaid, Money fee) {
        ensureActive();
        LocalDate previousDueDate = this.dueDate;

        double interestPerDay = interestAmount.amount() * 1.0 / termDays;
        double daysCovered = interestPaid.amount() / interestPerDay;

        LocalDate newDueDate = this.dueDate.plusDays(Math.round(daysCovered));
        this.dueDate = newDueDate;

        PawnContractExtension extension = PawnContractExtension.of(this, previousDueDate, newDueDate, interestPaid, fee);
        extensions.add(extension);
        return extension;
    }

    /**
     * Mark the contract as redeemed, setting the redeemedAt timestamp.
     */
    public void redeem() {
        ensureActive();
        this.status = PawnContractStatus.REDEEMED;
        this.redeemedAt = OffsetDateTime.now();
    }

    /**
     * Mark the contract as forfeited, setting the forfeitedAt timestamp.
     */
    public void forfeit() {
        ensureActive();
        this.status = PawnContractStatus.FORFEITED;
        this.forfeitedAt = OffsetDateTime.now();
    }

    // ===== HELPER FUNCTIONS =====

    public Money totalDue() {
        return principalAmount.add(interestAmount);
    }

    public boolean isActive() {
        return status == PawnContractStatus.ACTIVE;
    }

    public boolean isMatured() {
        return LocalDate.now().isAfter(dueDate);
    }

    public long daysOverdue() {
        if (!isMatured()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    private void ensureActive() {
        if (status != PawnContractStatus.ACTIVE) {
            throw new IllegalStateException("Operation not allowed on a " + status + " pawn contract");
        }
    }
}
