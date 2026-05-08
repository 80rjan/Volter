package com.volter.shop.report;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.reporting.application.dto.MonthlyReportCreationData;
import com.volter.shop.modules.reporting.application.dto.MonthlyReportItemBreakdownCreationData;
import com.volter.shop.modules.reporting.domain.model.MonthlyReport;
import com.volter.shop.modules.reporting.domain.model.MonthlyReportBreakdown;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MonthlyReportTest {

    private Staff manager() {
        return mock(Staff.class);
    }

    private MonthlyReportItemBreakdownCreationData itemBreakdown(TransactionCategory category, ItemType type) {
        return new MonthlyReportItemBreakdownCreationData(category, type, 5, 3000, 2000, 1000, 1500, 200, 1300);
    }

    private MonthlyReportCreationData data(List<MonthlyReportItemBreakdownCreationData> breakdowns) {
        return MonthlyReportCreationData.builder()
                .year(2026).month(5)
                .itemBreakdowns(breakdowns)
                .build();
    }

    // =========================================================================
    // create()
    // =========================================================================

    @Nested
    class Create {

        @Test
        void sets_year() {
            assertThat(MonthlyReport.create(data(List.of()), manager()).getYear()).isEqualTo(2026);
        }

        @Test
        void sets_month() {
            assertThat(MonthlyReport.create(data(List.of()), manager()).getMonth()).isEqualTo(5);
        }

        @Test
        void sets_manager() {
            Staff manager = manager();
            assertThat(MonthlyReport.create(data(List.of()), manager).getManager()).isSameAs(manager);
        }

        @Test
        void emptyBreakdowns_producesEmptyList() {
            assertThat(MonthlyReport.create(data(List.of()), manager()).getBreakdowns()).isEmpty();
        }

        @Test
        void singleBreakdown_addedToReport() {
            var items = List.of(itemBreakdown(TransactionCategory.SALE, ItemType.GOLD));
            assertThat(MonthlyReport.create(data(items), manager()).getBreakdowns()).hasSize(1);
        }

        @Test
        void multipleBreakdowns_allAdded() {
            var items = List.of(
                    itemBreakdown(TransactionCategory.SALE, ItemType.GOLD),
                    itemBreakdown(TransactionCategory.PAWN, ItemType.ELECTRONIC)
            );
            assertThat(MonthlyReport.create(data(items), manager()).getBreakdowns()).hasSize(2);
        }

        @Test
        void breakdown_category_isMapped() {
            var items = List.of(itemBreakdown(TransactionCategory.SALE, ItemType.GOLD));
            MonthlyReportBreakdown bd = MonthlyReport.create(data(items), manager()).getBreakdowns().get(0);
            assertThat(bd.getCategory()).isEqualTo(TransactionCategory.SALE);
        }

        @Test
        void breakdown_itemType_isMapped() {
            var items = List.of(itemBreakdown(TransactionCategory.SALE, ItemType.GOLD));
            MonthlyReportBreakdown bd = MonthlyReport.create(data(items), manager()).getBreakdowns().get(0);
            assertThat(bd.getItemType()).isEqualTo(ItemType.GOLD);
        }

        @Test
        void breakdown_count_isMapped() {
            var items = List.of(itemBreakdown(TransactionCategory.SALE, ItemType.GOLD));
            MonthlyReportBreakdown bd = MonthlyReport.create(data(items), manager()).getBreakdowns().get(0);
            assertThat(bd.getCount()).isEqualTo(5);
        }

        @Test
        void breakdown_turnover_isMapped() {
            var items = List.of(itemBreakdown(TransactionCategory.SALE, ItemType.GOLD));
            MonthlyReportBreakdown bd = MonthlyReport.create(data(items), manager()).getBreakdowns().get(0);
            assertThat(bd.getTurnover()).isEqualTo(3000);
        }

        @Test
        void breakdown_revenue_isMapped() {
            var items = List.of(itemBreakdown(TransactionCategory.SALE, ItemType.GOLD));
            MonthlyReportBreakdown bd = MonthlyReport.create(data(items), manager()).getBreakdowns().get(0);
            assertThat(bd.getRevenue()).isEqualTo(2000);
        }

        @Test
        void breakdown_cashOut_isMapped() {
            var items = List.of(itemBreakdown(TransactionCategory.SALE, ItemType.GOLD));
            MonthlyReportBreakdown bd = MonthlyReport.create(data(items), manager()).getBreakdowns().get(0);
            assertThat(bd.getCashOut()).isEqualTo(1000);
        }

        @Test
        void breakdown_grossProfit_isMapped() {
            var items = List.of(itemBreakdown(TransactionCategory.SALE, ItemType.GOLD));
            MonthlyReportBreakdown bd = MonthlyReport.create(data(items), manager()).getBreakdowns().get(0);
            assertThat(bd.getGrossProfit()).isEqualTo(1500);
        }

        @Test
        void breakdown_expenses_isMapped() {
            var items = List.of(itemBreakdown(TransactionCategory.SALE, ItemType.GOLD));
            MonthlyReportBreakdown bd = MonthlyReport.create(data(items), manager()).getBreakdowns().get(0);
            assertThat(bd.getExpenses()).isEqualTo(200);
        }

        @Test
        void breakdown_netProfit_isMapped() {
            var items = List.of(itemBreakdown(TransactionCategory.SALE, ItemType.GOLD));
            MonthlyReportBreakdown bd = MonthlyReport.create(data(items), manager()).getBreakdowns().get(0);
            assertThat(bd.getNetProfit()).isEqualTo(1300);
        }

        @Test
        void breakdown_linkedBackToReport() {
            var items = List.of(itemBreakdown(TransactionCategory.SALE, ItemType.GOLD));
            MonthlyReport report = MonthlyReport.create(data(items), manager());
            assertThat(report.getBreakdowns().get(0).getMonthlyReport()).isSameAs(report);
        }

        @Test
        void generatedAt_isNullBeforePrePersist() {
            assertThat(MonthlyReport.create(data(List.of()), manager()).getGeneratedAt()).isNull();
        }
    }
}
