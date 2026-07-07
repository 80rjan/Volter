package skit_project.tests.workflow;

import com.volter.identity.modules.staff.domain.repository.StaffRepository;
import com.volter.shared.config.SchemaInitializer;
import com.volter.shared.multitenancy.TenantContext;
import com.volter.shop.VolterApplication;
import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterRepository;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterSessionRepository;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.application.dto.CustomerCreateRequest;
import com.volter.shop.modules.inventory.application.dto.ItemCreateRequest;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.pawn.application.PawnService;
import com.volter.shop.modules.pawn.application.dto.PawnCreateRequest;
import com.volter.shop.modules.pawn.application.dto.PawnExtendRequest;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.PawnContractExtension;
import com.volter.shop.modules.pawn.domain.model.enums.PawnContractStatus;
import com.volter.shop.modules.pawn.domain.repository.PawnContractExtensionRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.modules.transaction.domain.repository.TransactionRepository;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SPRING BOOT — full-workflow (end-to-end) integration test.
 *
 * Unlike the @WebMvcTest slice (mocked service, web layer only) and the unit tests (all
 * collaborators mocked), this boots the WHOLE application against a REAL PostgreSQL
 * (Testcontainers) and drives the pawn-EXTENSION workflow through the real service layer end
 * to end — no mocks. It proves that the collaborating modules (pawn, inventory, customer,
 * cash register, transaction) actually work together and persist the right state.
 *
 * Workflow under test:  create a pawn contract  ->  extend it.
 *   create : discloses the principal OUT of the cash session, opens an ACTIVE contract.
 *   extend : collects interest IN, moves the due date forward, records the extension.
 *
 * The test runs in its own throwaway tenant schema (provisioned via SchemaInitializer and
 * dropped in teardown); staff FKs resolve to the bootstrap "user" in the public schema. It
 * lives outside com.volter, so @SpringBootTest is pointed at VolterApplication explicitly.
 *
 * Requires Docker (Testcontainers). It is a *IntegrationTest, so Surefire (`./mvnw test`)
 * skips it and Failsafe runs it:  ./mvnw verify   (or  ./mvnw -Dit.test=PawnExtensionWorkflowIntegrationTest failsafe:integration-test).
 */
@SpringBootTest(classes = VolterApplication.class)
@DisplayName("Spring Boot end-to-end — pawn extension workflow")
class PawnExtensionWorkflowIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES;
    private static final String EXTERNAL_URL = System.getenv("IT_DATASOURCE_URL");

    static {
        if (EXTERNAL_URL == null) {
            POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
            POSTGRES.start();
        } else {
            POSTGRES = null;
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        if (POSTGRES != null) {
            registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES::getUsername);
            registry.add("spring.datasource.password", POSTGRES::getPassword);
        } else {
            registry.add("spring.datasource.url", () -> EXTERNAL_URL);
            registry.add("spring.datasource.username", () -> envOrDefault("IT_DATASOURCE_USERNAME", "test"));
            registry.add("spring.datasource.password", () -> envOrDefault("IT_DATASOURCE_PASSWORD", "test"));
        }
        registry.add("jwt.secret", () -> "integration-test-secret-key-at-least-32-bytes!!");
    }

    private static String envOrDefault(String key, String fallback) {
        String v = System.getenv(key);
        return v == null ? fallback : v;
    }

    @Autowired private PawnService pawnService;
    @Autowired private CustomerService customerService;
    @Autowired private PawnContractRepository contractRepository;
    @Autowired private PawnContractExtensionRepository extensionRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private CashRegisterRepository cashRegisterRepository;
    @Autowired private CashRegisterSessionRepository sessionRepository;
    @Autowired private StaffRepository staffRepository;
    @Autowired private SchemaInitializer schemaInitializer;
    @Autowired private DataSource dataSource;

    private static final LocalDate ISSUE_DATE = LocalDate.now();
    private static final int TERM_DAYS = 30;
    private static final int PRINCIPAL = 1000;
    private static final int INTEREST = 100;
    private static final int OPENING_BALANCE = 5000;

    private String tenant;
    private Long staffId;
    private Long customerId;
    private Long sessionId;

    @BeforeEach
    void seedTenantAndPrerequisites() {
        tenant = "shop_wf_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        schemaInitializer.initializeTenantSchema(tenant);
        TenantContext.setCurrentTenant(tenant);

        staffId = staffRepository.findByUsername("user").orElseThrow().getId();

        // An open cash register session to move money against, and a customer to pawn for.
        CashRegister register = cashRegisterRepository.saveAndFlush(CashRegister.create("REG-WF"));
        CashRegisterSession session = sessionRepository.saveAndFlush(
                CashRegisterSession.open(register, staffId, OPENING_BALANCE, new Money(0)));
        sessionId = session.getId();

        customerId = customerService.create(new CustomerCreateRequest(
                "Ана Ангелова", "1234567890123", "070111222", null, "ул. Прва 1", "Скопје")).getId();
    }

    @AfterEach
    void dropTenant() {
        TenantContext.clear();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP SCHEMA IF EXISTS " + tenant + " CASCADE");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to drop test schema " + tenant, e);
        }
    }

    @Test
    @DisplayName("create -> extend: due date moves forward, interest is collected, extension is recorded")
    void extensionWorkflow_endToEnd() {
        // ----- Step 1: create a pawn contract (principal paid OUT of the session) --------------
        ItemCreateRequest item = new ItemCreateRequest(
                ItemType.GOLD, ItemOriginType.PAWN, ItemStatus.IN_PAWN, "Златен прстен",
                Map.of("weightGrams", 5.0));
        PawnContract created = pawnService.create(new PawnCreateRequest(
                customerId, item, PRINCIPAL, INTEREST, TERM_DAYS, ISSUE_DATE, sessionId), staffId);
        Long contractId = created.getId();

        assertThat(created.getStatus()).isEqualTo(PawnContractStatus.ACTIVE);
        assertThat(created.getDueDate()).isEqualTo(ISSUE_DATE.plusDays(TERM_DAYS));
        // principal (1000) disbursed OUT: 5000 - 1000 = 4000
        assertThat(sessionRepository.findById(sessionId).orElseThrow().getCurrentBalance())
                .isEqualTo(OPENING_BALANCE - PRINCIPAL);

        // A fresh tenant schema is seeded with demo data (see db/seed/tenant/V2__seed_demo_data.sql),
        // so we assert on DELTAS and on our own contract, never on absolute table counts.
        long extensionsBefore = extensionRepository.count();

        // ----- Step 2: extend the contract (interest paid IN, due date pushed forward) ---------
        // interestPaid == the contract's interest, so daysCovered == TERM_DAYS -> +30 days.
        PawnContractExtension extension = pawnService.extend(
                contractId, new PawnExtendRequest(INTEREST, 0, sessionId), staffId);

        assertThat(extension.getPreviousDueDate()).isEqualTo(ISSUE_DATE.plusDays(TERM_DAYS));
        assertThat(extension.getNewDueDate()).isEqualTo(ISSUE_DATE.plusDays(TERM_DAYS + TERM_DAYS));

        // ----- Step 3: assert the whole workflow's persisted side effects ----------------------
        PawnContract reloaded = contractRepository.findById(contractId).orElseThrow();
        assertThat(reloaded.getDueDate())
                .as("due date is pushed to the extension's new due date")
                .isEqualTo(ISSUE_DATE.plusDays(TERM_DAYS + TERM_DAYS));
        assertThat(reloaded.getStatus()).isEqualTo(PawnContractStatus.ACTIVE);

        // exactly one NEW extension row was written by this workflow (delta, seed-immune)
        assertThat(extensionRepository.count())
                .as("one extension added by extend()")
                .isEqualTo(extensionsBefore + 1);

        // and it is our extension, persisted with the workflow's dates
        PawnContractExtension persisted = extensionRepository.findById(extension.getId()).orElseThrow();
        assertThat(persisted.getNewDueDate()).isEqualTo(ISSUE_DATE.plusDays(TERM_DAYS + TERM_DAYS));
        assertThat(persisted.getInterestPaid()).isEqualTo(new Money(INTEREST));

        // interest (100) collected IN on our session: 4000 + 100 = 4100
        assertThat(sessionRepository.findById(sessionId).orElseThrow().getCurrentBalance())
                .isEqualTo(OPENING_BALANCE - PRINCIPAL + INTEREST);

        // a PAWN inflow of exactly the interest was recorded on our session for the extension
        assertThat(transactionRepository.findAll())
                .anySatisfy(tx -> {
                    assertThat(tx.getType()).isEqualTo(TransactionType.PAWN);
                    assertThat(tx.getDirection()).isEqualTo(TransactionDirection.IN);
                    assertThat(tx.getAmount()).isEqualTo(new Money(INTEREST));
                    assertThat(tx.getCashRegisterSession().getId()).isEqualTo(sessionId);
                });
    }
}
