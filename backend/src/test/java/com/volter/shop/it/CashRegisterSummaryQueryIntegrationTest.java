package com.volter.shop.it;

import com.volter.identity.modules.staff.domain.repository.StaffRepository;
import com.volter.shared.config.SchemaInitializer;
import com.volter.shared.multitenancy.TenantContext;
import com.volter.shop.modules.cashregister.application.dto.CashRegisterSessionSummary;
import com.volter.shop.modules.cashregister.application.dto.CashRegisterSummary;
import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterTransaction;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterTransactionAction;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterRepository;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterSessionRepository;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterTransactionRepository;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.modules.transaction.domain.repository.TransactionRepository;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves the cash-register reporting aggregations on {@link CashRegisterTransactionRepository}
 * compute correct numbers at runtime against a real PostgreSQL. Bootstrap only validates that
 * the constructor-expression projections parse; this test verifies the {@code sum/case/coalesce}
 * branches, the direction- vs action-based buckets, the {@code cast(createdAt as date)} window
 * filter, and the {@code Long} DTO mapping actually behave.
 *
 * <p>Runs inside its own throwaway tenant schema (provisioned via {@link SchemaInitializer} and
 * dropped in teardown) so the aggregation sees only this test's rows. The {@code staff_id} FKs
 * resolve to the bootstrap {@code user} in the shared public schema.
 *
 * <p>Transactions recorded (type CASH_REGISTER):
 * <pre>
 *   IN  100  DEPOSIT
 *   IN   50  DEPOSIT
 *   IN   10  ADJUSTMENT
 *   OUT  35  WITHDRAWAL
 *   OUT  20  ADJUSTMENT
 * </pre>
 * giving inflow 160, outflow 55, net 105; deposits 150, withdrawals 35, adjustments 30.
 */
class CashRegisterSummaryQueryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CashRegisterRepository cashRegisterRepository;
    @Autowired
    private CashRegisterSessionRepository sessionRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CashRegisterTransactionRepository cashTxRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private SchemaInitializer schemaInitializer;
    @Autowired
    private DataSource dataSource;

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate WINDOW_FROM = TODAY.minusDays(1);
    private static final LocalDate WINDOW_TO = TODAY.plusDays(1);

    private String tenant;
    private Long sessionId;

    @BeforeEach
    void seedSchema() {
        tenant = "shop_sum_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        schemaInitializer.initializeTenantSchema(tenant);
        TenantContext.setCurrentTenant(tenant);

        Long staffId = staffRepository.findByUsername("user").orElseThrow().getId();

        CashRegister register = cashRegisterRepository.saveAndFlush(CashRegister.create("REG-SUM"));
        CashRegisterSession session = sessionRepository.saveAndFlush(
                CashRegisterSession.open(register, staffId, 0, new Money(0)));
        sessionId = session.getId();

        record(session, staffId, 100, TransactionDirection.IN, CashRegisterTransactionAction.DEPOSIT);
        record(session, staffId, 50, TransactionDirection.IN, CashRegisterTransactionAction.DEPOSIT);
        record(session, staffId, 10, TransactionDirection.IN, CashRegisterTransactionAction.ADJUSTMENT);
        record(session, staffId, 35, TransactionDirection.OUT, CashRegisterTransactionAction.WITHDRAWAL);
        record(session, staffId, 20, TransactionDirection.OUT, CashRegisterTransactionAction.ADJUSTMENT);
    }

    @AfterEach
    void dropSchema() {
        TenantContext.clear();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP SCHEMA IF EXISTS " + tenant + " CASCADE");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to drop test schema " + tenant, e);
        }
    }

    @Test
    void summarize_bucketsByDirectionAndAction() {
        CashRegisterSummary summary = cashTxRepository.summarize(WINDOW_FROM, WINDOW_TO);

        assertThat(summary.totalTransactions()).isEqualTo(5L);
        assertThat(summary.inflow()).isEqualTo(160L);
        assertThat(summary.outflow()).isEqualTo(55L);
        assertThat(summary.net()).isEqualTo(105L);
        assertThat(summary.deposits()).isEqualTo(150L);
        assertThat(summary.withdrawals()).isEqualTo(35L);
        assertThat(summary.adjustments()).isEqualTo(30L);
    }

    @Test
    void summarize_outsideTheDateWindowReturnsCoalescedZeros() {
        CashRegisterSummary summary = cashTxRepository.summarize(TODAY.minusDays(10), TODAY.minusDays(5));

        assertThat(summary.totalTransactions()).isEqualTo(0L);
        assertThat(summary.inflow()).isEqualTo(0L);
        assertThat(summary.outflow()).isEqualTo(0L);
        assertThat(summary.net()).isEqualTo(0L);
        assertThat(summary.deposits()).isEqualTo(0L);
        assertThat(summary.withdrawals()).isEqualTo(0L);
        assertThat(summary.adjustments()).isEqualTo(0L);
    }

    @Test
    void summarizeSessions_groupsPerSessionWithTheOpenedDate() {
        List<CashRegisterSessionSummary> summaries = cashTxRepository.summarizeSessions(WINDOW_FROM, WINDOW_TO);

        assertThat(summaries).hasSize(1);
        CashRegisterSessionSummary row = summaries.getFirst();
        assertThat(row.sessionId()).isEqualTo(sessionId);
        assertThat(row.date()).isEqualTo(TODAY);
        assertThat(row.totalTransactions()).isEqualTo(5L);
        assertThat(row.inflow()).isEqualTo(160L);
        assertThat(row.outflow()).isEqualTo(55L);
        assertThat(row.net()).isEqualTo(105L);
    }

    @Test
    void summarizeSessions_outsideTheDateWindowIsEmpty() {
        assertThat(cashTxRepository.summarizeSessions(TODAY.minusDays(10), TODAY.minusDays(5))).isEmpty();
    }

    // ----- helpers -----

    private void record(CashRegisterSession session, Long staffId, int amount,
                        TransactionDirection direction, CashRegisterTransactionAction action) {
        Transaction tx = transactionRepository.saveAndFlush(
                Transaction.create(staffId, session, TransactionType.CASH_REGISTER,
                        new Money(amount), direction, action.name()));
        cashTxRepository.saveAndFlush(CashRegisterTransaction.record(tx, action));
    }
}
