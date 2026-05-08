package com.volter.shop.modules.reporting.web.response;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyReportBreakdownResponse {
    @NotNull(message = "Category is required")
    private TransactionCategory category;

    @NotNull(message = "Type is required")
    private ItemType itemType;

    @NotNull(message = "Count is required")
    private Integer count;

    @NotNull(message = "Turnover is required")
    private Integer turnover;      // revenue + cash out (total money moving through the shop)

    @NotNull(message = "Cash out is required")
    private Integer cashOut;

    @NotNull(message = "Revenue is required")
    private Integer revenue;

    @NotNull(message = "Gross profit is required")
    private Integer grossProfit;

    @NotNull(message = "Expenses is required")
    private Integer expenses;

    @NotNull(message = "Net profit is required")
    private Integer netProfit;
}
