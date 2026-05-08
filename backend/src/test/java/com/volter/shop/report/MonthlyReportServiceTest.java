package com.volter.shop.report;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.reporting.application.MonthlyReportService;
import com.volter.shop.modules.reporting.domain.model.MonthlyReport;
import com.volter.shop.modules.reporting.infrastructure.repository.MonthlyReportRepository;
import com.volter.shop.modules.reporting.web.request.MonthlyReportFilterRequest;
import com.volter.shop.modules.staff.application.StaffService;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import com.volter.shop.modules.transaction.infrastructure.repository.TransactionAggregateRow;
import com.volter.shop.modules.transaction.infrastructure.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonthlyReportServiceTest {

    @Mock private MonthlyReportRepository monthlyReportRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private StaffService staffService;

    @InjectMocks
    private MonthlyReportService monthlyReportService;

    private MonthlyReport report;
    private Staff manager;

    @BeforeEach
    void setUp() {
        manager = mock(Staff.class);
        report = MonthlyReport.builder().id(1L).year(2026).month(5).build();
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private TransactionAggregateRow aggregateRow(String category, String type, int count,
                                                  int turnover, int revenue, int cashOut,
                                                  int grossProfit, int expenses, int netProfit) {
        TransactionAggregateRow row = mock(TransactionAggregateRow.class);
        lenient().when(row.getCategory()).thenReturn(category);
        lenient().when(row.getType()).thenReturn(type);
        lenient().when(row.getCount()).thenReturn(count);
        lenient().when(row.getTurnover()).thenReturn(turnover);
        lenient().when(row.getRevenue()).thenReturn(revenue);
        lenient().when(row.getCashOut()).thenReturn(cashOut);
        lenient().when(row.getGrossProfit()).thenReturn(grossProfit);
        lenient().when(row.getExpenses()).thenReturn(expenses);
        lenient().when(row.getNetProfit()).thenReturn(netProfit);
        return row;
    }

    private void stubGenerate(List<TransactionAggregateRow> rows) {
        when(monthlyReportRepository.existsByYearAndMonth(2026, 5)).thenReturn(false);
        when(staffService.getCurrentStaff()).thenReturn(manager);
        when(transactionRepository.aggregateByMonthAndYear(2026, 5)).thenReturn(rows);
        when(monthlyReportRepository.save(any(MonthlyReport.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // =========================================================================
    // getAll()
    // =========================================================================

    @Nested
    @DisplayName("getAll() with filters and pagination")
    class GetAll {

        @Test
        @DisplayName("returns page from repository")
        void returnsPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<MonthlyReport> page = new PageImpl<>(List.of(report), pageable, 1);

            when(monthlyReportRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            assertThat(monthlyReportService.getAll(new MonthlyReportFilterRequest(null, null), pageable).getTotalElements()).isEqualTo(1);
            verify(monthlyReportRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("empty filter returns all reports")
        void emptyFilter_returnsAll() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<MonthlyReport> page = new PageImpl<>(List.of(report, mock(MonthlyReport.class)));

            when(monthlyReportRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            assertThat(monthlyReportService.getAll(new MonthlyReportFilterRequest(null, null), pageable).getContent()).hasSize(2);
        }

        @Test
        @DisplayName("year filter is passed to specification")
        void yearFilter_passedToSpec() {
            Pageable pageable = PageRequest.of(0, 10);
            when(monthlyReportRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(new PageImpl<>(List.of(report)));

            monthlyReportService.getAll(new MonthlyReportFilterRequest(2026, null), pageable);

            verify(monthlyReportRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("month filter is passed to specification")
        void monthFilter_passedToSpec() {
            Pageable pageable = PageRequest.of(0, 10);
            when(monthlyReportRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(new PageImpl<>(List.of(report)));

            monthlyReportService.getAll(new MonthlyReportFilterRequest(null, 5), pageable);

            verify(monthlyReportRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("empty result page returns empty content")
        void emptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            when(monthlyReportRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));

            assertThat(monthlyReportService.getAll(new MonthlyReportFilterRequest(2024, 1), pageable).isEmpty()).isTrue();
        }

        @Test
        @DisplayName("second page returns correct offset")
        void pagination_secondPage() {
            Pageable pageable = PageRequest.of(1, 5);
            Page<MonthlyReport> page = new PageImpl<>(List.of(report), pageable, 10);
            when(monthlyReportRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<MonthlyReport> result = monthlyReportService.getAll(new MonthlyReportFilterRequest(null, null), pageable);

            assertThat(result.getNumber()).isEqualTo(1);
            assertThat(result.getTotalElements()).isEqualTo(10);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }
    }

    // =========================================================================
    // getById()
    // =========================================================================

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        void returnsReport_whenFound() {
            when(monthlyReportRepository.findById(1L)).thenReturn(Optional.of(report));
            assertThat(monthlyReportService.getById(1L)).isEqualTo(report);
        }

        @Test
        void throws_whenNotFound() {
            when(monthlyReportRepository.findById(999L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> monthlyReportService.getById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Monthly report not found");
        }
    }

    // =========================================================================
    // getByYearAndMonth()
    // =========================================================================

    @Nested
    @DisplayName("getByYearAndMonth()")
    class GetByYearAndMonth {

        @Test
        void returnsReport_whenFound() {
            when(monthlyReportRepository.findByYearAndMonth(2026, 5)).thenReturn(Optional.of(report));
            assertThat(monthlyReportService.getByYearAndMonth(2026, 5)).isEqualTo(report);
        }

        @Test
        void throws_whenNotFound() {
            when(monthlyReportRepository.findByYearAndMonth(2020, 1)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> monthlyReportService.getByYearAndMonth(2020, 1))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Monthly report not found");
        }
    }

    // =========================================================================
    // generate()
    // =========================================================================

    @Nested
    @DisplayName("generate()")
    class Generate {

        @Test
        void throws_whenReportAlreadyExists() {
            when(monthlyReportRepository.existsByYearAndMonth(2026, 5)).thenReturn(true);

            assertThatThrownBy(() -> monthlyReportService.generate(5, 2026))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("already exists");
            verify(monthlyReportRepository, never()).save(any());
        }

        @Test
        void callsGetCurrentStaff() {
            stubGenerate(List.of());
            monthlyReportService.generate(5, 2026);
            verify(staffService).getCurrentStaff();
        }

        @Test
        void callsAggregateByMonthAndYear_withCorrectArgs() {
            stubGenerate(List.of());
            monthlyReportService.generate(5, 2026);
            verify(transactionRepository).aggregateByMonthAndYear(2026, 5);
        }

        @Test
        void savesAndReturnsReport() {
            stubGenerate(List.of());
            monthlyReportService.generate(5, 2026);
            verify(monthlyReportRepository).save(any(MonthlyReport.class));
        }

        @Test
        void report_hasCorrectYear() {
            stubGenerate(List.of());
            assertThat(monthlyReportService.generate(5, 2026).getYear()).isEqualTo(2026);
        }

        @Test
        void report_hasCorrectMonth() {
            stubGenerate(List.of());
            assertThat(monthlyReportService.generate(5, 2026).getMonth()).isEqualTo(5);
        }

        @Test
        void report_hasCorrectManager() {
            stubGenerate(List.of());
            assertThat(monthlyReportService.generate(5, 2026).getManager()).isEqualTo(manager);
        }

        @Test
        void noAggregateRows_producesEmptyBreakdowns() {
            stubGenerate(List.of());
            assertThat(monthlyReportService.generate(5, 2026).getBreakdowns()).isEmpty();
        }

        @Test
        void rowWithNullType_isFiltered() {
            TransactionAggregateRow nullTypeRow = mock(TransactionAggregateRow.class);
            when(nullTypeRow.getType()).thenReturn(null);
            stubGenerate(List.of(nullTypeRow));

            assertThat(monthlyReportService.generate(5, 2026).getBreakdowns()).isEmpty();
        }

        @Test
        void validRow_producesOneBreakdown() {
            TransactionAggregateRow row = aggregateRow("SALE", "GOLD", 3, 2000, 1500, 500, 1200, 100, 1100);
            stubGenerate(List.of(row));

            assertThat(monthlyReportService.generate(5, 2026).getBreakdowns()).hasSize(1);
        }

        @Test
        void breakdown_hasMappedCategory() {
            TransactionAggregateRow row = aggregateRow("PAWN", "GOLD", 2, 1000, 800, 200, 600, 50, 550);
            stubGenerate(List.of(row));

            assertThat(monthlyReportService.generate(5, 2026).getBreakdowns().get(0).getCategory())
                    .isEqualTo(TransactionCategory.PAWN);
        }

        @Test
        void breakdown_hasMappedItemType() {
            TransactionAggregateRow row = aggregateRow("SALE", "ELECTRONIC", 1, 500, 400, 100, 300, 30, 270);
            stubGenerate(List.of(row));

            assertThat(monthlyReportService.generate(5, 2026).getBreakdowns().get(0).getItemType())
                    .isEqualTo(ItemType.ELECTRONIC);
        }

        @Test
        void mixedRows_onlyNonNullTypesIncluded() {
            TransactionAggregateRow valid = aggregateRow("SALE", "GOLD", 3, 2000, 1500, 500, 1200, 100, 1100);
            TransactionAggregateRow nullType = mock(TransactionAggregateRow.class);
            when(nullType.getType()).thenReturn(null);
            stubGenerate(List.of(valid, nullType));

            assertThat(monthlyReportService.generate(5, 2026).getBreakdowns()).hasSize(1);
        }

        @Test
        void multipleValidRows_allBecomeSeparateBreakdowns() {
            TransactionAggregateRow row1 = aggregateRow("SALE", "GOLD", 3, 2000, 1500, 500, 1200, 100, 1100);
            TransactionAggregateRow row2 = aggregateRow("PAWN", "ELECTRONIC", 1, 800, 600, 200, 500, 80, 420);
            stubGenerate(List.of(row1, row2));

            assertThat(monthlyReportService.generate(5, 2026).getBreakdowns()).hasSize(2);
        }
    }
}
