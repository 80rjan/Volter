package com.volter.shop.modules.reporting.domain.model;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyReportBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Monthly report item breakdown category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionCategory category;

    @NotNull(message = "Monthly report item breakdown type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ItemType itemType;

    @NotNull(message = "Monthly report item breakdown count is required")
    @Column(nullable = false)
    private Integer count;

    @NotNull(message = "Monthly report item breakdown turnover is required")
    @Column(nullable = false)
    private Integer turnover;      // revenue + cash out (total money moving through the shop)

    @NotNull(message = "Monthly report item breakdown cash out is required")
    @Column(nullable = false)
    private Integer cashOut;

    @NotNull(message = "Monthly report item breakdown revenue is required")
    @Column(nullable = false)
    private Integer revenue;

    @NotNull(message = "Monthly report item breakdown gross profit is required")
    @Column(nullable = false)
    private Integer grossProfit;

    @NotNull(message = "Monthly report item breakdown expenses is required")
    @Column(nullable = false)
    private Integer expenses;

    @NotNull(message = "Monthly report item breakdown net profit is required")
    @Column(nullable = false)
    private Integer netProfit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_report_id", nullable = false, foreignKey = @ForeignKey(name = "fk_monthly_report_item_breakdown_monthly_report"))
    private MonthlyReport monthlyReport;
}
