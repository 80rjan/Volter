package com.volter.platform.modules.report.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.platform.modules.report.application.dto.request.ReportFilterRequest;
import com.volter.platform.modules.report.application.dto.response.PeriodReportResponse;
import com.volter.platform.modules.report.application.dto.response.StaffPerformanceResponse;
import com.volter.shop.modules.expense.application.dto.ExpenseSummary;
import com.volter.platform.modules.report.domain.model.Report;
import com.volter.platform.modules.report.domain.repository.ReportRepository;
import com.volter.platform.modules.report.domain.specification.ReportSpecification;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import com.volter.shop.modules.cashregister.application.dto.StaffDiscrepancySummary;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterSessionDiscrepancyRepository;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterTransactionRepository;
import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;
import com.volter.shop.modules.expense.domain.repository.ExpenseTransactionRepository;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.sale.domain.model.SaleTransaction;
import com.volter.shop.modules.sale.domain.repository.SaleTransactionRepository;
import com.volter.shop.modules.transaction.application.dto.CashFlowSummary;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final PawnTransactionRepository pawnTransactionRepository;
    private final SaleTransactionRepository saleTransactionRepository;
    private final CashRegisterTransactionRepository cashRegisterTransactionRepository;
    private final CashRegisterSessionDiscrepancyRepository discrepancyRepository;
    private final ExpenseTransactionRepository expenseTransactionRepository;
    private final StaffService staffService;

    /**
     * List paginated reports, filtered by optional filters. Scoped to the caller's
     * shop (reports live in the shared public schema), then narrowed to system
     * reports (no owner) and reports they own.
     */
    public Page<Report> list(ReportFilterRequest filter, Pageable pageable, Long staffId, Long shopId) {
        return reportRepository.findAll(
                ReportSpecification.matches(filter)
                        .and(ReportSpecification.forShop(shopId))
                        .and(ReportSpecification.visibleTo(visibleStaffIds(staffId))),
                pageable);
    }

    /**
     * Get a single report by ID, only if it belongs to the caller's shop and the
     * caller may see it: system reports (no owner), or a staff-performance report
     * about the caller or one of their subordinates.
     */
    public Report get(Long id, Long staffId, Long shopId) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + id));

        boolean visible = report.getShopId().equals(shopId)
                && (report.getOwnerStaffId() == null || visibleStaffIds(staffId).contains(report.getSubjectStaffId()));
        if (!visible) {
            throw new BusinessRuleException("Access denied to report: " + id);
        }
        return report;
    }

    /** The caller plus everyone below them in the management tree (for report visibility). */
    private List<Long> visibleStaffIds(Long staffId) {
        List<Long> ids = new ArrayList<>(staffService.findSubordinateStaffIds(staffId));
        ids.add(staffId);
        return ids;
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
     * Computes the same summary as a monthly report, but for an arbitrary date range
     * and WITHOUT persisting it. Reads the caller's shop data via the request's tenant
     * context, so no shop id is needed.
     */
    @SuppressWarnings("unchecked")
    public PeriodReportResponse periodSummary(LocalDate from, LocalDate to) {
        // The freshly aggregated payload still holds typed records, so compute the
        // headline figures from them directly (the same formula the list mapper applies
        // to a persisted report's deserialized map). The payload serializes to the same
        // JSON shape for the response either way.
        Map<String, Object> payload = aggregateMonthlySummary(from, to);
        Map<ItemType, CashFlowSummary> pawns = (Map<ItemType, CashFlowSummary>) payload.get("pawns");
        Map<ItemType, CashFlowSummary> sales = (Map<ItemType, CashFlowSummary>) payload.get("sales");
        Map<ExpenseCategory, ExpenseSummary> expenses = (Map<ExpenseCategory, ExpenseSummary>) payload.get("expenses");

        long totalRevenue = sumFlow(pawns, CashFlowSummary::inflow) + sumFlow(sales, CashFlowSummary::inflow);
        long moneyGivenToClients = sumFlow(pawns, CashFlowSummary::outflow) + sumFlow(sales, CashFlowSummary::outflow);
        long totalExpenses = expenses.values().stream().mapToLong(ExpenseSummary::amount).sum();
        long netProfit = sumFlow(pawns, CashFlowSummary::net) + sumFlow(sales, CashFlowSummary::net) - totalExpenses;

        return new PeriodReportResponse(from, to, totalRevenue, totalExpenses, netProfit, moneyGivenToClients, payload);
    }

    private static long sumFlow(Map<?, CashFlowSummary> flows, java.util.function.ToLongFunction<CashFlowSummary> field) {
        return flows.values().stream().mapToLong(field).sum();
    }

    // ----- staff performance -----

    /**
     * Generates and persists a staff member's monthly performance report. The caller
     * MUST have set the tenant context to the shop's schema beforehand (the figures
     * read that shop's tenant-schema transactions).
     */
    @Transactional
    public Report generateStaffPerformance(Long shopId, Long staffId, LocalDate from, LocalDate to) {
        return reportRepository.save(Report.staffPerformance(
                shopId, staffId, from, to, toPayload(computeStaffPerformance(staffId, from, to))));
    }

    /**
     * Computes a staff member's performance for an arbitrary range WITHOUT persisting it,
     * for the caller's shop (tenant context). Visible to the staff themselves and to their
     * managers up the tree.
     */
    public StaffPerformanceResponse staffPerformancePeriod(Long staffId, LocalDate from, LocalDate to, Long callerStaffId) {
        if (!staffId.equals(callerStaffId) && !staffService.isManagerOf(callerStaffId, staffId)) {
            throw new BusinessRuleException("Access denied to this staff member's report");
        }
        return computeStaffPerformance(staffId, from, to);
    }

    /**
     * Derives a staff member's figures from the transactions they performed in the range.
     * Profit is attributable: pawn interest realized (extensions + redemption over principal)
     * and sale margin; expenses are tracked as a count only, not deducted.
     */
    public StaffPerformanceResponse computeStaffPerformance(Long staffId, LocalDate from, LocalDate to) {
        long revenue = 0, moneyGiven = 0, pawnProfit = 0, saleProfit = 0;
        long pawnsOpened = 0, pawnsExtended = 0, pawnsRedeemed = 0, openedPrincipalSum = 0, underpaidRedemptions = 0;
        long salesCreated = 0, salesSold = 0, underpricedSales = 0;

        for (PawnTransaction pt : pawnTransactionRepository.findForStaffInRange(staffId, from, to)) {
            Transaction tx = pt.getTransaction();
            int amount = tx.getAmount().amount();
            if (pt.getAction() == PawnTransactionAction.ADJUSTED) {
                // A principal correction is neither revenue nor a new loan: it only
                // nets into money handled — added when paid out, subtracted when returned.
                moneyGiven += tx.isInflow() ? -amount : amount;
            } else if (tx.isInflow()) {
                revenue += amount;
            } else {
                moneyGiven += amount;
            }
            switch (pt.getAction()) {
                case CONTRACT_CREATED -> { pawnsOpened++; openedPrincipalSum += pt.getPawnContract().getPrincipalAmount().amount(); }
                case EXTENDED -> { pawnsExtended++; pawnProfit += amount; }
                case REDEEMED -> {
                    pawnsRedeemed++;
                    var contract = pt.getPawnContract();
                    pawnProfit += amount - contract.getPrincipalAmount().amount();
                    // Underpaid = paid less than principal + interest owed. Compare against the
                    // amount due, NOT expectedRedemptionAmount(): that method adds a live overdue
                    // penalty based on today's date, which would wrongly flag past, already-settled
                    // redemptions whose due date has since passed.
                    long owed = contract.getPrincipalAmount().amount() + contract.getInterestAmount().amount();
                    if (amount < owed) underpaidRedemptions++;
                }
                case FORFEITED -> { /* forfeiture records no cash transaction; nothing to attribute */ }
            }
        }

        for (SaleTransaction st : saleTransactionRepository.findForStaffInRange(staffId, from, to)) {
            Transaction tx = st.getTransaction();
            int amount = tx.getAmount().amount();
            if (tx.isInflow()) revenue += amount; else moneyGiven += amount;
            switch (st.getAction()) {
                case LISTING_CREATED -> salesCreated++;
                case SOLD -> {
                    salesSold++;
                    Integer p = st.getSale().profit();
                    saleProfit += p == null ? 0 : p;
                    if (st.getSale().isUnderwater()) underpricedSales++;
                }
            }
        }

        long expensesRecorded = expenseTransactionRepository.countForStaffInRange(staffId, from, to);
        StaffDiscrepancySummary disc = discrepancyRepository.summarizeForStaff(staffId, from, to);
        long avgLoanSize = pawnsOpened > 0 ? openedPrincipalSum / pawnsOpened : 0;

        return new StaffPerformanceResponse(
                staffId, from, to,
                revenue, moneyGiven,
                pawnProfit, saleProfit, pawnProfit + saleProfit,
                pawnsOpened, pawnsExtended, pawnsRedeemed, salesCreated, salesSold, expensesRecorded,
                avgLoanSize, disc.count(), disc.totalDifference(),
                underpricedSales, underpaidRedemptions, underpricedSales + underpaidRedemptions);
    }

    /** The performance figures as a JSON-friendly map, for persisting in a report's payload. */
    private Map<String, Object> toPayload(StaffPerformanceResponse p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("revenue", p.revenue());
        m.put("moneyGiven", p.moneyGiven());
        m.put("pawnProfit", p.pawnProfit());
        m.put("saleProfit", p.saleProfit());
        m.put("totalProfit", p.totalProfit());
        m.put("pawnsOpened", p.pawnsOpened());
        m.put("pawnsExtended", p.pawnsExtended());
        m.put("pawnsRedeemed", p.pawnsRedeemed());
        m.put("salesCreated", p.salesCreated());
        m.put("salesSold", p.salesSold());
        m.put("expensesRecorded", p.expensesRecorded());
        m.put("avgLoanSize", p.avgLoanSize());
        m.put("discrepancyCount", p.discrepancyCount());
        m.put("discrepancyTotal", p.discrepancyTotal());
        m.put("underpricedSales", p.underpricedSales());
        m.put("underpaidRedemptions", p.underpaidRedemptions());
        m.put("riskFlags", p.riskFlags());
        return m;
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
