package com.volter.shop.modules.reporting.application;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.reporting.application.dto.MonthlyReportCreationData;
import com.volter.shop.modules.reporting.application.dto.MonthlyReportItemBreakdownCreationData;
import com.volter.shop.modules.reporting.domain.model.MonthlyReport;
import com.volter.shop.modules.reporting.domain.specification.MonthlyReportSpecification;
import com.volter.shop.modules.reporting.infrastructure.repository.MonthlyReportRepository;
import com.volter.shop.modules.reporting.web.request.MonthlyReportFilterRequest;
import com.volter.shop.modules.staff.application.StaffService;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import com.volter.shop.modules.transaction.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole(T(com.volter.identity.domain.model.enums.RoleEnum).MANAGER) or hasRole(T(com.volter.identity.domain.model.enums.RoleEnum).ADMIN)")
public class MonthlyReportService {

    private final MonthlyReportRepository monthlyReportRepository;
    private final TransactionRepository transactionRepository;
    private final StaffService staffService;

    @Transactional
    public Page<MonthlyReport> getAll(MonthlyReportFilterRequest filters, Pageable pageable) {
        Specification<MonthlyReport> spec = MonthlyReportSpecification.withFilters(filters);
        return monthlyReportRepository.findAll(spec, pageable);
    }

    @Transactional
    public MonthlyReport getById(Long id) {
        return monthlyReportRepository.findById(id).orElseThrow(() -> new RuntimeException("Monthly report not found"));
    }

    @Transactional
    public MonthlyReport getByYearAndMonth(Integer year, Integer month) {
        return monthlyReportRepository.findByYearAndMonth(year, month).orElseThrow(() -> new RuntimeException("Monthly report not found"));
    }

    @Transactional
    public MonthlyReport generate(Integer month, Integer year) {
        if (monthlyReportRepository.existsByYearAndMonth(year, month)) {
            throw new RuntimeException("Monthly report for this month already exists");
        }

        Staff manager = staffService.getCurrentStaff();

        List<MonthlyReportItemBreakdownCreationData> itemBreakdowns = transactionRepository
                .aggregateByMonthAndYear(year, month)
                .stream()
                .filter(row -> row.getType() != null)
                .map(row -> new MonthlyReportItemBreakdownCreationData(
                        TransactionCategory.valueOf(row.getCategory()),
                        ItemType.valueOf(row.getType()),
                        row.getCount(),
                        row.getTurnover(),
                        row.getRevenue(),
                        row.getCashOut(),
                        row.getGrossProfit(),
                        row.getExpenses(),
                        row.getNetProfit()
                ))
                .toList();

        MonthlyReportCreationData reportData = MonthlyReportCreationData.builder()
                .year(year)
                .month(month)
                .itemBreakdowns(itemBreakdowns)
                .build();

        MonthlyReport report = MonthlyReport.create(reportData, manager);
        return monthlyReportRepository.save(report);
    }
}
