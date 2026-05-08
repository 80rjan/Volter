package com.volter.shop.modules.expense.domain.model;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.expense.web.request.ExpenseCreationRequest;
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

    @Transient
    public ExpenseTransaction getInitialTransaction() {
        return transactions.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Initial creation transaction not found for pawn id: " + id));
    }

    public static Expense create(ExpenseCreationRequest request, Staff staff, CashRegisterSession cashRegisterSession) {
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
                .description(request.getTransactionDescription())
                .expense(expense)
                .staff(staff)
                .cashRegisterSession(cashRegisterSession)
                .build();
        expense.transactions.add(transaction);

        return expense;
    }
}
