package com.volter.platform.report;

import com.volter.platform.modules.report.application.MonthlyReportScheduler;
import com.volter.platform.modules.report.application.ReportService;
import com.volter.platform.modules.shop.domain.model.Shop;
import com.volter.platform.modules.shop.domain.repository.ShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonthlyReportSchedulerTest {

    @Mock
    private ShopRepository shopRepository;
    @Mock
    private ReportService reportService;

    @InjectMocks
    private MonthlyReportScheduler scheduler;

    @Test
    @DisplayName("generates a monthly summary for each active shop and skips inactive ones")
    void generatesForActiveShops() {
        Shop a = mock(Shop.class);
        when(a.isActive()).thenReturn(true);
        when(a.getId()).thenReturn(1L);
        when(a.getSchemaName()).thenReturn("shop_a");
        Shop b = mock(Shop.class);
        when(b.isActive()).thenReturn(true);
        when(b.getId()).thenReturn(2L);
        when(b.getSchemaName()).thenReturn("shop_b");
        Shop inactive = mock(Shop.class);
        when(inactive.isActive()).thenReturn(false);
        when(shopRepository.findAll()).thenReturn(List.of(a, b, inactive));

        scheduler.generateMonthlyReports();

        verify(reportService).generateMonthlySummary(eq(1L), any(), any());
        verify(reportService).generateMonthlySummary(eq(2L), any(), any());
        verify(reportService, never()).generateMonthlySummary(eq(3L), any(), any());
    }
}
