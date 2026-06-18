package com.volter.shop.modules.transaction.domain.model;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Base ledger entry. Every movement of money in a shop is a transaction tied to
 * a cash register session and a staff member (identity context, by id). The
 * concrete business meaning is carried by a 1:1 subtype-link entity
 * (pawn / sale / expense / cash register), keyed by {@link #type}.
 */
@Entity
@Table(name = "transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Staff is required")
    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @NotNull(message = "Cash register session is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cash_register_session_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_crs"))
    private CashRegisterSession cashRegisterSession;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @NotNull(message = "Transaction amount is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false))
    private Money amount;

    @NotNull(message = "Transaction direction is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private TransactionDirection direction;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public static Transaction create(Long staffId,
                                     CashRegisterSession session,
                                     TransactionType type,
                                     Money amount,
                                     TransactionDirection direction,
                                     String description) {
        return Transaction.builder()
                .staffId(staffId)
                .cashRegisterSession(session)
                .type(type)
                .amount(amount)
                .direction(direction)
                .description(description)
                .build();
    }

    public boolean isInflow() {
        return direction == TransactionDirection.IN;
    }

    public boolean isOutflow() {
        return direction == TransactionDirection.OUT;
    }

    /** Amount signed by direction: positive for inflows, negative for outflows. */
    public int signedAmount() {
        int magnitude = amount.amount();
        return isInflow() ? magnitude : -magnitude;
    }
}
