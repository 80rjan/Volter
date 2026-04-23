package com.volter.shop.modules.reporting.domain.model;

import com.volter.shop.modules.reporting.domain.model.enums.MonthlyReportItemBreakdownCategory;
import com.volter.shop.modules.reporting.domain.model.enums.MonthlyReportItemBreakdownType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MonthlyReportItemBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Monthly report item breakdown category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MonthlyReportItemBreakdownCategory category;

    @NotNull(message = "Monthly report item breakdown type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MonthlyReportItemBreakdownType type;

    @NotNull(message = "Monthly report item breakdown count is required")
    @Column(nullable = false)
    private Integer count;

    @NotNull(message = "Monthly report item breakdown revenue is required")
    @Column(nullable = false)
    private Integer revenue;

    @NotNull(message = "Monthly report item breakdown profit is required")
    @Column(nullable = false)
    private Integer profit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_report_id", nullable = false, foreignKey = @ForeignKey(name = "fk_monthly_report_item_breakdown_monthly_report"))
    private MonthlyReport monthlyReport;
}
