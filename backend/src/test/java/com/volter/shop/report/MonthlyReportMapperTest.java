package com.volter.shop.report;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.reporting.domain.model.MonthlyReport;
import com.volter.shop.modules.reporting.domain.model.MonthlyReportBreakdown;
import com.volter.shop.modules.reporting.infrastructure.mapper.MonthlyReportBreakdownMapper;
import com.volter.shop.modules.reporting.infrastructure.mapper.MonthlyReportBreakdownMapperImpl;
import com.volter.shop.modules.reporting.infrastructure.mapper.MonthlyReportMapper;
import com.volter.shop.modules.reporting.infrastructure.mapper.MonthlyReportMapperImpl;
import com.volter.shop.modules.reporting.web.response.MonthlyReportBreakdownResponse;
import com.volter.shop.modules.reporting.web.response.MonthlyReportDetailedResponse;
import com.volter.shop.modules.reporting.web.response.MonthlyReportResponse;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        MonthlyReportMapperImpl.class,
        MonthlyReportBreakdownMapperImpl.class
})
class MonthlyReportMapperTest {

    @Autowired
    private MonthlyReportMapper monthlyReportMapper;

    @Autowired
    private MonthlyReportBreakdownMapper monthlyReportBreakdownMapper;

    // ─── fixtures ─────────────────────────────────────────────────────────────

    private MonthlyReportBreakdown breakdown(int turnover, int cashOut, int revenue,
                                              int grossProfit, int expenses, int netProfit) {
        return MonthlyReportBreakdown.builder()
                .category(TransactionCategory.SALE)
                .itemType(ItemType.GOLD)
                .count(3)
                .turnover(turnover)
                .cashOut(cashOut)
                .revenue(revenue)
                .grossProfit(grossProfit)
                .expenses(expenses)
                .netProfit(netProfit)
                .build();
    }

    private MonthlyReport report(List<MonthlyReportBreakdown> breakdowns) {
        return MonthlyReport.builder()
                .id(1L)
                .year(2026)
                .month(5)
                .breakdowns(breakdowns)
                .build();
    }

    // =========================================================================
    // toResponse() — aggregated summary with @AfterMapping
    // =========================================================================

    @Nested
    @DisplayName("toResponse() — aggregated summary")
    class ToResponse {

        @Test
        @DisplayName("maps id")
        void mapsId() {
            assertThat(monthlyReportMapper.toResponse(report(List.of())).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("maps year")
        void mapsYear() {
            assertThat(monthlyReportMapper.toResponse(report(List.of())).getYear()).isEqualTo(2026);
        }

        @Test
        @DisplayName("maps month")
        void mapsMonth() {
            assertThat(monthlyReportMapper.toResponse(report(List.of())).getMonth()).isEqualTo(5);
        }

        @Test
        @DisplayName("empty breakdowns produces zero for all financial totals")
        void emptyBreakdowns_zeroTotals() {
            MonthlyReportResponse response = monthlyReportMapper.toResponse(report(List.of()));
            assertThat(response.getTurnover()).isZero();
            assertThat(response.getCashOut()).isZero();
            assertThat(response.getRevenue()).isZero();
            assertThat(response.getGrossProfit()).isZero();
            assertThat(response.getExpenses()).isZero();
            assertThat(response.getNetProfit()).isZero();
        }

        @Test
        @DisplayName("single breakdown — totals equal breakdown values")
        void singleBreakdown_totalsEqualBreakdownValues() {
            MonthlyReportResponse response = monthlyReportMapper.toResponse(
                    report(List.of(breakdown(3000, 1000, 2000, 1500, 200, 1300))));
            assertThat(response.getTurnover()).isEqualTo(3000);
            assertThat(response.getCashOut()).isEqualTo(1000);
            assertThat(response.getRevenue()).isEqualTo(2000);
            assertThat(response.getGrossProfit()).isEqualTo(1500);
            assertThat(response.getExpenses()).isEqualTo(200);
            assertThat(response.getNetProfit()).isEqualTo(1300);
        }

        @Test
        @DisplayName("turnover is sum of all breakdown turnovers")
        void turnover_isSumOfBreakdowns() {
            var breakdowns = List.of(
                    breakdown(1000, 400, 600, 500, 100, 400),
                    breakdown(2000, 800, 1200, 1000, 200, 800)
            );
            assertThat(monthlyReportMapper.toResponse(report(breakdowns)).getTurnover()).isEqualTo(3000);
        }

        @Test
        @DisplayName("cashOut is sum of all breakdown cashOuts")
        void cashOut_isSumOfBreakdowns() {
            var breakdowns = List.of(
                    breakdown(1000, 300, 700, 500, 100, 400),
                    breakdown(2000, 600, 1400, 1000, 200, 800)
            );
            assertThat(monthlyReportMapper.toResponse(report(breakdowns)).getCashOut()).isEqualTo(900);
        }

        @Test
        @DisplayName("revenue is sum of all breakdown revenues")
        void revenue_isSumOfBreakdowns() {
            var breakdowns = List.of(
                    breakdown(1000, 400, 600, 400, 100, 300),
                    breakdown(2000, 800, 1200, 800, 200, 600)
            );
            assertThat(monthlyReportMapper.toResponse(report(breakdowns)).getRevenue()).isEqualTo(1800);
        }

        @Test
        @DisplayName("grossProfit is sum of all breakdown grossProfits")
        void grossProfit_isSumOfBreakdowns() {
            var breakdowns = List.of(
                    breakdown(1000, 400, 600, 500, 100, 400),
                    breakdown(2000, 800, 1200, 1000, 200, 800)
            );
            assertThat(monthlyReportMapper.toResponse(report(breakdowns)).getGrossProfit()).isEqualTo(1500);
        }

        @Test
        @DisplayName("expenses is sum of all breakdown expenses")
        void expenses_isSumOfBreakdowns() {
            var breakdowns = List.of(
                    breakdown(1000, 400, 600, 500, 150, 350),
                    breakdown(2000, 800, 1200, 1000, 250, 750)
            );
            assertThat(monthlyReportMapper.toResponse(report(breakdowns)).getExpenses()).isEqualTo(400);
        }

        @Test
        @DisplayName("netProfit is sum of all breakdown netProfits")
        void netProfit_isSumOfBreakdowns() {
            var breakdowns = List.of(
                    breakdown(1000, 400, 600, 500, 150, 350),
                    breakdown(2000, 800, 1200, 1000, 250, 750)
            );
            assertThat(monthlyReportMapper.toResponse(report(breakdowns)).getNetProfit()).isEqualTo(1100);
        }
    }

    // =========================================================================
    // toDetailedResponse()
    // =========================================================================

    @Nested
    @DisplayName("toDetailedResponse() — with breakdowns list")
    class ToDetailedResponse {

        private MonthlyReportBreakdown fullBreakdown() {
            return MonthlyReportBreakdown.builder()
                    .category(TransactionCategory.PAWN)
                    .itemType(ItemType.ELECTRONIC)
                    .count(4)
                    .turnover(5000)
                    .cashOut(2000)
                    .revenue(3000)
                    .grossProfit(2500)
                    .expenses(300)
                    .netProfit(2200)
                    .build();
        }

        @Test
        @DisplayName("maps id")
        void mapsId() {
            assertThat(monthlyReportMapper.toDetailedResponse(report(List.of())).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("maps year")
        void mapsYear() {
            assertThat(monthlyReportMapper.toDetailedResponse(report(List.of())).getYear()).isEqualTo(2026);
        }

        @Test
        @DisplayName("maps month")
        void mapsMonth() {
            assertThat(monthlyReportMapper.toDetailedResponse(report(List.of())).getMonth()).isEqualTo(5);
        }

        @Test
        @DisplayName("empty breakdowns maps to empty list")
        void emptyBreakdowns_mapsToEmptyList() {
            assertThat(monthlyReportMapper.toDetailedResponse(report(List.of())).getBreakdowns()).isEmpty();
        }

        @Test
        @DisplayName("one breakdown maps to one MonthlyReportBreakdownResponse")
        void oneBreakdown_mapsToOneBreakdownResponse() {
            var result = monthlyReportMapper.toDetailedResponse(report(List.of(fullBreakdown())));
            assertThat(result.getBreakdowns()).hasSize(1);
            assertThat(result.getBreakdowns().get(0)).isInstanceOf(MonthlyReportBreakdownResponse.class);
        }

        @Test
        @DisplayName("multiple breakdowns all appear in the response list")
        void multipleBreakdowns_allPresent() {
            var result = monthlyReportMapper.toDetailedResponse(report(List.of(fullBreakdown(), fullBreakdown())));
            assertThat(result.getBreakdowns()).hasSize(2);
        }

        @Test
        @DisplayName("sub-mapper maps breakdown category")
        void breakdown_category_isMapped() {
            var bd = monthlyReportMapper.toDetailedResponse(report(List.of(fullBreakdown()))).getBreakdowns().get(0);
            assertThat(bd.getCategory()).isEqualTo(TransactionCategory.PAWN);
        }

        @Test
        @DisplayName("sub-mapper maps breakdown itemType")
        void breakdown_itemType_isMapped() {
            var bd = monthlyReportMapper.toDetailedResponse(report(List.of(fullBreakdown()))).getBreakdowns().get(0);
            assertThat(bd.getItemType()).isEqualTo(ItemType.ELECTRONIC);
        }

        @Test
        @DisplayName("sub-mapper maps breakdown count")
        void breakdown_count_isMapped() {
            var bd = monthlyReportMapper.toDetailedResponse(report(List.of(fullBreakdown()))).getBreakdowns().get(0);
            assertThat(bd.getCount()).isEqualTo(4);
        }

        @Test
        @DisplayName("sub-mapper maps breakdown turnover")
        void breakdown_turnover_isMapped() {
            var bd = monthlyReportMapper.toDetailedResponse(report(List.of(fullBreakdown()))).getBreakdowns().get(0);
            assertThat(bd.getTurnover()).isEqualTo(5000);
        }

        @Test
        @DisplayName("sub-mapper maps breakdown cashOut")
        void breakdown_cashOut_isMapped() {
            var bd = monthlyReportMapper.toDetailedResponse(report(List.of(fullBreakdown()))).getBreakdowns().get(0);
            assertThat(bd.getCashOut()).isEqualTo(2000);
        }

        @Test
        @DisplayName("sub-mapper maps breakdown revenue")
        void breakdown_revenue_isMapped() {
            var bd = monthlyReportMapper.toDetailedResponse(report(List.of(fullBreakdown()))).getBreakdowns().get(0);
            assertThat(bd.getRevenue()).isEqualTo(3000);
        }

        @Test
        @DisplayName("sub-mapper maps breakdown grossProfit")
        void breakdown_grossProfit_isMapped() {
            var bd = monthlyReportMapper.toDetailedResponse(report(List.of(fullBreakdown()))).getBreakdowns().get(0);
            assertThat(bd.getGrossProfit()).isEqualTo(2500);
        }

        @Test
        @DisplayName("sub-mapper maps breakdown expenses")
        void breakdown_expenses_isMapped() {
            var bd = monthlyReportMapper.toDetailedResponse(report(List.of(fullBreakdown()))).getBreakdowns().get(0);
            assertThat(bd.getExpenses()).isEqualTo(300);
        }

        @Test
        @DisplayName("sub-mapper maps breakdown netProfit")
        void breakdown_netProfit_isMapped() {
            var bd = monthlyReportMapper.toDetailedResponse(report(List.of(fullBreakdown()))).getBreakdowns().get(0);
            assertThat(bd.getNetProfit()).isEqualTo(2200);
        }

        @Test
        @DisplayName("each breakdown in a multi-breakdown report is independently mapped")
        void multipleBreakdowns_eachMappedIndependently() {
            MonthlyReportBreakdown first = MonthlyReportBreakdown.builder()
                    .category(TransactionCategory.SALE).itemType(ItemType.GOLD)
                    .count(2).turnover(1000).cashOut(400).revenue(600).grossProfit(500).expenses(100).netProfit(400)
                    .build();
            MonthlyReportBreakdown second = MonthlyReportBreakdown.builder()
                    .category(TransactionCategory.PAWN).itemType(ItemType.ELECTRONIC)
                    .count(5).turnover(3000).cashOut(1000).revenue(2000).grossProfit(1800).expenses(200).netProfit(1600)
                    .build();

            var breakdowns = monthlyReportMapper.toDetailedResponse(report(List.of(first, second))).getBreakdowns();

            assertThat(breakdowns.get(0).getCategory()).isEqualTo(TransactionCategory.SALE);
            assertThat(breakdowns.get(0).getItemType()).isEqualTo(ItemType.GOLD);
            assertThat(breakdowns.get(0).getCount()).isEqualTo(2);
            assertThat(breakdowns.get(1).getCategory()).isEqualTo(TransactionCategory.PAWN);
            assertThat(breakdowns.get(1).getItemType()).isEqualTo(ItemType.ELECTRONIC);
            assertThat(breakdowns.get(1).getCount()).isEqualTo(5);
        }
    }

    // =========================================================================
    // MonthlyReportBreakdownMapper.toResponse()
    // =========================================================================

    @Nested
    @DisplayName("MonthlyReportBreakdownMapper.toResponse()")
    class BreakdownToResponse {

        private MonthlyReportBreakdown fullBreakdown() {
            return MonthlyReportBreakdown.builder()
                    .category(TransactionCategory.PAWN)
                    .itemType(ItemType.ELECTRONIC)
                    .count(7)
                    .turnover(5000)
                    .cashOut(2000)
                    .revenue(3000)
                    .grossProfit(2500)
                    .expenses(300)
                    .netProfit(2200)
                    .build();
        }

        @Test
        @DisplayName("maps category")
        void mapsCategory() {
            assertThat(monthlyReportBreakdownMapper.toResponse(fullBreakdown()).getCategory())
                    .isEqualTo(TransactionCategory.PAWN);
        }

        @Test
        @DisplayName("maps itemType")
        void mapsItemType() {
            assertThat(monthlyReportBreakdownMapper.toResponse(fullBreakdown()).getItemType())
                    .isEqualTo(ItemType.ELECTRONIC);
        }

        @Test
        @DisplayName("maps count")
        void mapsCount() {
            assertThat(monthlyReportBreakdownMapper.toResponse(fullBreakdown()).getCount()).isEqualTo(7);
        }

        @Test
        @DisplayName("maps turnover")
        void mapsTurnover() {
            assertThat(monthlyReportBreakdownMapper.toResponse(fullBreakdown()).getTurnover()).isEqualTo(5000);
        }

        @Test
        @DisplayName("maps cashOut")
        void mapsCashOut() {
            assertThat(monthlyReportBreakdownMapper.toResponse(fullBreakdown()).getCashOut()).isEqualTo(2000);
        }

        @Test
        @DisplayName("maps revenue")
        void mapsRevenue() {
            assertThat(monthlyReportBreakdownMapper.toResponse(fullBreakdown()).getRevenue()).isEqualTo(3000);
        }

        @Test
        @DisplayName("maps grossProfit")
        void mapsGrossProfit() {
            assertThat(monthlyReportBreakdownMapper.toResponse(fullBreakdown()).getGrossProfit()).isEqualTo(2500);
        }

        @Test
        @DisplayName("maps expenses")
        void mapsExpenses() {
            assertThat(monthlyReportBreakdownMapper.toResponse(fullBreakdown()).getExpenses()).isEqualTo(300);
        }

        @Test
        @DisplayName("maps netProfit")
        void mapsNetProfit() {
            assertThat(monthlyReportBreakdownMapper.toResponse(fullBreakdown()).getNetProfit()).isEqualTo(2200);
        }
    }
}
