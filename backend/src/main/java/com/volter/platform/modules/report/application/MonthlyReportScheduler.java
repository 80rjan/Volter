package com.volter.platform.modules.report.application;

import com.volter.platform.modules.shop.domain.model.Shop;
import com.volter.platform.modules.shop.domain.repository.ShopRepository;
import com.volter.shared.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Generates the previous month's shop-wide summary as a system report for every
 * active shop, on the 1st of each month.
 * <p>
 * A scheduled job has no request and therefore no tenant context, so this loops the
 * shops itself and sets the tenant context to each shop's schema before generating —
 * the aggregation reads that shop's tenant-schema data, while the report row is written
 * to the public schema.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyReportScheduler {

    private final ShopRepository shopRepository;
    private final ReportService reportService;

    @Scheduled(cron = "0 0 0 1 * *", zone = "Europe/Skopje")
    public void generateMonthlyReports() {
        LocalDate firstOfThisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate from = firstOfThisMonth.minusMonths(1);  // 1st of the previous month
        LocalDate to = firstOfThisMonth.minusDays(1);      // last day of the previous month

        // Read the shop list before any tenant is set, so it resolves against the public schema.
        List<Shop> shops = shopRepository.findAll().stream().filter(Shop::isActive).toList();
        log.info("Generating monthly summaries for {} active shop(s), period {} .. {}", shops.size(), from, to);

        for (Shop shop : shops) {
            try {
                TenantContext.setCurrentTenant(shop.getSchemaName());
                reportService.generateMonthlySummary(shop.getId(), from, to);
            } catch (Exception e) {
                log.error("Failed to generate monthly summary for shop {}", shop.getId(), e);
            } finally {
                TenantContext.clear();
            }
        }
    }
}
