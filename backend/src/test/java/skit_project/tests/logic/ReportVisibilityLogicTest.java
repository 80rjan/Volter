package skit_project.tests.logic;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.platform.modules.report.application.ReportService;
import com.volter.platform.modules.report.domain.model.Report;
import com.volter.platform.modules.report.domain.repository.ReportRepository;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterSessionDiscrepancyRepository;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterTransactionRepository;
import com.volter.shop.modules.expense.domain.repository.ExpenseTransactionRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.sale.domain.repository.SaleTransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * LOGIC COVERAGE (GACC / CACC / RACC)
 * Unit under test: ReportService.get(id, staffId, shopId)
 *
 * This predicate is a MIXED 3-clause predicate, chosen because — unlike the 2-clause
 * conjunction in GoldFilterLogicTest (where GACC = CACC = RACC) — here RACC is strictly
 * stronger than GACC/CACC. Full derivation lives in logic-explanation-report.txt.
 *
 *   P = a && (b || c)
 *     a : report.getShopId().equals(shopId)                              -- report is in caller's shop
 *     b : report.getOwnerStaffId() == null                              -- system report (no owner)
 *     c : visibleStaffIds(staffId).contains(report.getSubjectStaffId()) -- subject is caller or subordinate
 *
 *   P == true  -> get() returns the report
 *   P == false -> get() throws BusinessRuleException ("Access denied")
 *
 * Determination (when each clause is active / "major"):
 *   a active <=> (b || c) == true      -- THREE minor solutions (b,c) in {TT, TF, FT}
 *   b active <=> a==true && c==false   -- minors forced
 *   c active <=> a==true && b==false   -- minors forced
 *
 * The 4 rows below are the RACC test set (so they also satisfy CACC and GACC):
 *   r2 (a=T,b=T,c=F) P=T     r3 (a=T,b=F,c=T) P=T
 *   r4 (a=T,b=F,c=F) P=F     r6 (a=F,b=T,c=F) P=F
 *
 * Clause pairs witnessed by this set:
 *   a : (r2,r6) identical minors (b=T,c=F) -> RACC   |  (r3,r6) minors differ -> GACC/CACC only
 *   b : (r2,r4) identical minors (a=T,c=F) -> RACC = CACC = GACC (minors forced)
 *   c : (r3,r4) identical minors (a=T,b=F) -> RACC = CACC = GACC (minors forced)
 *
 * Short-circuit note: b is read only when a is true; c is read only when a is true AND b is
 * false. Each test stubs only the getters actually reached on its row (otherwise Mockito's
 * strict stubbing would fail with "unnecessary stubbing").
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Logic coverage (GACC/CACC/RACC) — ReportService.get() predicate a && (b || c)")
class ReportVisibilityLogicTest {

    @Mock private ReportRepository reportRepository;
    @Mock private PawnTransactionRepository pawnTransactionRepository;
    @Mock private SaleTransactionRepository saleTransactionRepository;
    @Mock private CashRegisterTransactionRepository cashRegisterTransactionRepository;
    @Mock private CashRegisterSessionDiscrepancyRepository discrepancyRepository;
    @Mock private ExpenseTransactionRepository expenseTransactionRepository;
    @Mock private StaffService staffService;

    @InjectMocks private ReportService reportService;

    private static final long REPORT_ID    = 1L;
    private static final long CALLER_SHOP   = 10L;
    private static final long OTHER_SHOP    = 20L;   // a = F (report in a different shop)
    private static final long CALLER_STAFF  = 100L;
    private static final long SUBORDINATE   = 101L;  // in visibleStaffIds -> c can be true
    private static final long OUTSIDER      = 999L;  // not visible        -> c = false
    private static final long SOME_OWNER    = 500L;  // non-null owner     -> b = false

    /** A stored report whose clause-driving getters are stubbed per test. */
    private Report storedReport() {
        Report report = mock(Report.class);
        when(reportRepository.findById(REPORT_ID)).thenReturn(Optional.of(report));
        return report;
    }

    private Report call() {
        return reportService.get(REPORT_ID, CALLER_STAFF, CALLER_SHOP);
    }


    @Test
    @DisplayName("r2 (a=T, b=T, c=F) -> P=T : in-shop system report is returned (b short-circuits c)")
    void r2_true_inShopSystemReport_returned() {
        Report report = storedReport();
        when(report.getShopId()).thenReturn(CALLER_SHOP);    // a = T
        when(report.getOwnerStaffId()).thenReturn(null);     // b = T -> (b||c) true, c not evaluated

        assertSame(report, call());
    }

    @Test
    @DisplayName("r6 (a=F, b=T, c=F) -> P=F : wrong shop denies access (a short-circuits b,c)")
    void r6_false_wrongShop_denied() {
        Report report = storedReport();
        when(report.getShopId()).thenReturn(OTHER_SHOP);     // a = F -> short-circuit, deny

        assertThrows(BusinessRuleException.class, this::call);
    }

    @Test
    @DisplayName("r3 (a=T, b=F, c=T) -> P=T : owned report whose subject is a subordinate is returned")
    void r3_true_subjectIsSubordinate_returned() {
        Report report = storedReport();
        when(report.getShopId()).thenReturn(CALLER_SHOP);           // a = T
        when(report.getOwnerStaffId()).thenReturn(SOME_OWNER);      // b = F -> evaluate c
        when(staffService.findSubordinateStaffIds(CALLER_STAFF)).thenReturn(List.of(SUBORDINATE));
        when(report.getSubjectStaffId()).thenReturn(SUBORDINATE);   // c = T

        assertSame(report, call());
    }

    @Test
    @DisplayName("r4 (a=T, b=F, c=F) -> P=F : owned report about an outsider denies access")
    void r4_false_subjectNotVisible_denied() {
        Report report = storedReport();
        when(report.getShopId()).thenReturn(CALLER_SHOP);           // a = T
        when(report.getOwnerStaffId()).thenReturn(SOME_OWNER);      // b = F -> evaluate c
        when(staffService.findSubordinateStaffIds(CALLER_STAFF)).thenReturn(List.of(SUBORDINATE));
        when(report.getSubjectStaffId()).thenReturn(OUTSIDER);      // c = F -> deny

        assertThrows(BusinessRuleException.class, this::call);
    }
}
