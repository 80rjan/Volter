package com.volter.shop.modules.expense.domain.model;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.expense.application.dto.request.ExpenseCreationRequest;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.expense.domain.model.enums.ExpenseType;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionMarginType;
import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private Money amount;

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

    @NotNull(message = "Expense staff is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = true, foreignKey = @ForeignKey(name = "fk_expense_staff"))
    private Staff staff;

    @Builder.Default
    @OneToMany(mappedBy = "expense", cascade = {CascadeType.PERSIST}, orphanRemoval = false)
    private List<ExpenseTransaction> transactions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Expense create(ExpenseCreationRequest request, String transactionDescription, Staff staff, CashRegisterSession cashRegisterSession) {
        Expense expense = Expense.builder()
                .expenseType(request.getExpenseType())
                .amount(new Money(request.getAmount()))
                .description(request.getDescription())
                .date(request.getDate())
                .staff(staff)
                .build();

        ExpenseTransaction transaction = ExpenseTransaction.builder()
                .amount(new Money(request.getAmount()))
                .direction(TransactionDirection.OUT)
                .marginAmount(new Money(0))
                .marginType(TransactionMarginType.NEUTRAL)
                .description(transactionDescription)
                .expense(expense)
                .staff(staff)
                .cashRegisterSession(cashRegisterSession)
                .build();
        expense.transactions.add(transaction);

        return expense;
    }
}
