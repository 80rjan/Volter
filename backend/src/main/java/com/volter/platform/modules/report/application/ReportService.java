package com.volter.platform.modules.report.application;

import com.volter.platform.modules.report.application.dto.request.ReportFilterRequest;
import com.volter.shop.modules.expense.application.dto.ExpenseSummary;
import com.volter.platform.modules.report.domain.model.Report;
import com.volter.platform.modules.report.domain.repository.ReportRepository;
import com.volter.platform.modules.report.domain.specification.ReportSpecification;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterTransactionRepository;
import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;
import com.volter.shop.modules.expense.domain.repository.ExpenseTransactionRepository;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.sale.domain.repository.SaleTransactionRepository;
import com.volter.shop.modules.transaction.application.dto.CashFlowSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final PawnTransactionRepository pawnTransactionRepository;
    private final SaleTransactionRepository saleTransactionRepository;
    private final CashRegisterTransactionRepository cashRegisterTransactionRepository;
    private final ExpenseTransactionRepository expenseTransactionRepository;

    /**
     * List paginated reports, filtered by optional filters.
     * Reports the caller may see: system reports (no owner) and reports they own, narrowed by the filter.
     */
    public Page<Report> list(ReportFilterRequest filter, Pageable pageable, Long staffId) {
        return reportRepository.findAll(
                ReportSpecification.matches(filter).and(ReportSpecification.visibleTo(staffId)),
                pageable);
    }

    /**
     * Get a single report by ID, only if it is a system report or owned by the caller.
     */
    public Report get(Long id, Long staffId) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + id));

        if (!report.isVisibleTo(staffId)) {
            throw new BusinessRuleException("Access denied to report: " + id);
        }

        return report;
    }

    /**
     * Generates and persists the shop-wide monthly summary for {@code [from, to]} as a
     * system report (no owner). The caller MUST have set the tenant context to the shop's
     * schema beforehand, because the aggregation reads that shop's tenant-schema data; the
     * report row itself lands in the public schema (Report is pinned there).
     */
    @Transactional
    public Report generateMonthlySummary(Long shopId, LocalDate from, LocalDate to) {
        Map<String, Object> payload = aggregateMonthlySummary(from, to);
        return reportRepository.save(Report.systemMonthlySummary(shopId, from, to, payload));
    }

    /**
     * Aggregates the shop's monthly summary figures for {@code [from, to]} from its tenant-schema data.
     */
    private Map<String, Object> aggregateMonthlySummary(LocalDate from, LocalDate to) {
        HashMap<String, Object> summary = new HashMap<>();

        HashMap<ItemType, CashFlowSummary> pawnSummary = new HashMap<>();
        for (ItemType itemType : ItemType.values()) {
            CashFlowSummary data = pawnTransactionRepository.summarize(from, to, itemType);
            pawnSummary.put(itemType, data);
        }
        summary.put("pawns", pawnSummary);

        HashMap<ItemType, CashFlowSummary> saleSummary = new HashMap<>();
        for (ItemType itemType : ItemType.values()) {
            CashFlowSummary data = saleTransactionRepository.summarize(from, to, itemType);
            saleSummary.put(itemType, data);
        }
        summary.put("sales", saleSummary);

        HashMap<ExpenseCategory, ExpenseSummary> expenseSummary = new HashMap<>();
        for (ExpenseCategory expenseCategory : ExpenseCategory.values()) {
            ExpenseSummary data = expenseTransactionRepository.summarize(from, to, expenseCategory);
            expenseSummary.put(expenseCategory, data);
        }
        summary.put("expenses", expenseSummary);

        summary.put("cashRegister", cashRegisterTransactionRepository.summarize(from, to));
        summary.put("sessions", cashRegisterTransactionRepository.summarizeSessions(from, to));

        return summary;
    }
}
