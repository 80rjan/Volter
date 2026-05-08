package com.volter.shop.modules.reporting.domain.model;

import com.volter.shop.modules.reporting.application.dto.MonthlyReportCreationData;
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

    //---------MAPPINGS---------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false, foreignKey = @ForeignKey(name = "fk_monthly_report_manager"))
    private Staff manager;

    @Builder.Default
    @OneToMany(mappedBy = "monthlyReport", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MonthlyReportBreakdown> breakdowns = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.generatedAt = LocalDateTime.now();
    }

    public static MonthlyReport create(MonthlyReportCreationData data, Staff manager) {
        MonthlyReport monthlyReport = MonthlyReport.builder()
                .year(data.year())
                .month(data.month())
                .manager(manager)
                .build();

        data.itemBreakdowns().forEach(
                item -> {
                    MonthlyReportBreakdown breakdown = MonthlyReportBreakdown.builder()
                            .category(item.category())
                            .itemType(item.type())
                            .count(item.count())
                            .turnover(item.turnover())
                            .cashOut(item.cashOut())
                            .revenue(item.revenue())
                            .grossProfit(item.grossProfit())
                            .expenses(item.expenses())
                            .netProfit(item.netProfit())
                            .monthlyReport(monthlyReport)
                            .build();
                    monthlyReport.getBreakdowns().add(breakdown);
                });

        return monthlyReport;
    }
}