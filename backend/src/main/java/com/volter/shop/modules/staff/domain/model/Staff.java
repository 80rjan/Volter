package com.volter.shop.modules.staff.domain.model;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSessionAdjustment;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.modules.alert.domain.RiskAlert;
import com.volter.shop.modules.reporting.domain.model.MonthlyReport;
import com.volter.shop.modules.pawn.domain.model.event.PawnEvent;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.shared.valueobject.Compensation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Table(
//        indexes = {
//                @Index(name = "idx_staff_username", columnList = "username"),
//                @Index(name = "idx_staff_embg", columnList = "embg"),
//                @Index(name = "idx_staff_phone_number", columnList = "phoneNumber"),
//        },
//        uniqueConstraints = {
//                @UniqueConstraint(name = "uk_staff_username", columnNames = "username"),
//                @UniqueConstraint(name = "uk_staff_embg", columnNames = "embg"),
//                @UniqueConstraint(name = "uk_staff_phone_number", columnNames = "phoneNumber")
//        }
//)
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // Reference to identity user
    @NotNull(message = "Identity user ID is required")
    @Column(nullable = false, unique = true)    // todo: this in unique index in @Table
    private Long identityUserId;

    @NotNull(message = "Staff base salary is required")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "baseSalary", column = @Column(name = "base_salary", nullable = false)),
            @AttributeOverride(name = "bonusPercent", column = @Column(name = "bonus_percent", nullable = false))
    })
    private Compensation compensation;

    @NotNull(message = "Staff created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Staff updated at is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @NotNull(message = "Staff is deleted is required")
    @Column(nullable = false)
    private boolean deleted;

    @Builder.Default
    @OneToMany(mappedBy = "staff", cascade = {}, orphanRemoval = false)
    private List<Transaction> transactions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "staff", cascade = {}, orphanRemoval = false)
    private List<CashRegisterSession> cashRegisterSessions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "createdBy", cascade = {}, orphanRemoval = false)     // only for managers or higher
    private List<CashRegisterSessionAdjustment> cashRegisterSessionAdjustments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_id", nullable = true, foreignKey = @ForeignKey(name = "fk_staff_manager"))
    private Staff manager;

    @Builder.Default
    @OneToMany(mappedBy = "manager")
    private List<Staff> subordinates = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "staff", cascade = {}, orphanRemoval = false)
    private List<Expense> expenses = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "manager", orphanRemoval = false)
    private List<RiskAlert> riskAlerts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "manager", cascade = {}, orphanRemoval = false)
    private List<MonthlyReport> monthlyReports = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "performedBy", cascade = {}, orphanRemoval = false)
    private List<PawnEvent> pawnEvents = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        deleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
