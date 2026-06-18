package com.volter.shop.modules.expense.domain.model;

import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;
import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * An operating cost incurred by the shop (rent, utilities, supplies, ...),
 * recorded by a staff member (identity context, by id).
 */
@Entity
@Table(name = "expense")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Staff is required")
    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ExpenseCategory category;

    @NotNull(message = "Amount is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false))
    private Money amount;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Date is required")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public static Expense create(Long staffId, ExpenseCategory category, Money amount, String description, LocalDate date) {
        return Expense.builder()
                .staffId(staffId)
                .category(category)
                .amount(amount)
                .description(description)
                .date(date)
                .build();
    }
}
