package com.volter.backend.expense;

import com.volter.backend.staff.Staff;
import com.volter.backend.expense.enums.ExpenseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_expense_date", columnList = "date DESC"),
                @Index(name = "idx_expense_staff_id", columnList = "staff_id"),
                @Index(name = "idx_expense_type_date", columnList = "expenseType, date DESC")
        }
)
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Expense type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseType expenseType;

    @NotNull(message = "Expense amount is required")
    @Column(nullable = false)
    private Integer amount;

    @NotNull(message = "Expense description is required")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "Expense date is required")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Expense created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Expense updated at is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = true, foreignKey = @ForeignKey(name = "fk_expense_staff"))
    private Staff staff;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
