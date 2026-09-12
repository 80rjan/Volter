package com.volter.shop.modules.transaction.domain.model;

import com.volter.shop.modules.transaction.domain.model.enums.ActivityKind;
import com.volter.shop.modules.transaction.domain.model.enums.ActivityType;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;

/**
 * One row of the activity list, read from the {@code activity_entry} database
 * view: every ledger {@link Transaction} plus every non-monetary pawn contract
 * event, in a single relation so the page can sort and paginate across both.
 *
 * <p>Read-only — the view is not insertable. Write through the underlying
 * entities. On an event row {@code amount}, {@code direction} and
 * {@code cashRegisterSessionId} are null, which is what marks it as "not money".
 *
 * <p>{@code entryId} prefixes the source id ({@code T123} / {@code E45}) so the
 * two id spaces cannot collide; {@code sourceId} is the id within that source.
 */
@Entity
@Immutable
@Table(name = "activity_entry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityEntry {

    @Id
    @Column(name = "entry_id")
    private String entryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false)
    private ActivityKind kind;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @Column(name = "cash_register_session_id")
    private Long cashRegisterSessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ActivityType type;

    @Column(name = "amount")
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private TransactionDirection direction;

    @Column(name = "description")
    private String description;

    // Mapped from the view's occurred_at. Named createdAt so the API's existing
    // `sort=createdAt` keeps working unchanged.
    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime createdAt;

    public boolean isTransaction() {
        return kind == ActivityKind.TRANSACTION;
    }
}
