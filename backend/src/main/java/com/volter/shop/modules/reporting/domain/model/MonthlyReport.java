package com.volter.shop.modules.reporting.domain.model;

import com.volter.shop.modules.staff.domain.model.Staff;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Monthly report year is required")
    @Column(nullable = false)
    private Integer year;

    @NotNull(message = "Monthly report month is required")
    @Column(nullable = false)
    private Integer month;

    @NotNull(message = "Monthly report generated at is required")
    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @NotNull(message = "Monthly report total sales is required")
    @Column(nullable = false)
    private Integer totalSales;

    @NotNull(message = "Monthly report total pawns is required")
    @Column(nullable = false)
    private Integer totalPawns;

    @NotNull(message = "Monthly report total turnover is required")
    @Column(nullable = false)
    private Integer totalTurnover;

    @NotNull(message = "Monthly report total revenue is required")
    @Column(nullable = false)
    private Integer totalRevenue;       // cash in + profit from transactions

    @NotNull(message = "Monthly report total cash in is required")
    @Column(nullable = false)
    private Integer totalCashIn;

    @NotNull(message = "Monthly report total cash out is required")
    @Column(nullable = false)
    private Integer totalCashOut;

    @NotNull(message = "Monthly report gross profit is required")
    @Column(nullable = false)
    private Integer grossProfit;

    @NotNull(message = "Monthly report total expenses is required")
    @Column(nullable = false)
    private Integer totalExpenses;

    @NotNull(message = "Monthly report net profit is required")
    @Column(nullable = false)
    private Integer netProfit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false, foreignKey = @ForeignKey(name = "fk_monthly_report_manager"))
    private Staff manager;

    @Builder.Default
    @OneToMany(mappedBy = "monthlyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MonthlyReportItemBreakdown> itemBreakdowns = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.generatedAt = LocalDateTime.now();
    }
}