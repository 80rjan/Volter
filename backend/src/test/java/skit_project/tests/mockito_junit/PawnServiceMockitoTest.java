package skit_project.tests.mockito_junit;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.application.dto.ItemCreateRequest;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.notification.application.NotificationService;
import com.volter.shop.modules.notification.domain.model.enums.NotificationType;
import com.volter.shop.modules.pawn.application.PawnService;
import com.volter.shop.modules.pawn.application.dto.*;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.PawnContractExtension;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.repository.PawnContractExtensionRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.shared.valueobject.Money;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mockito + JUnit — PawnService")
class PawnServiceMockitoTest {

    @Mock private PawnContractRepository contractRepository;
    @Mock private PawnContractExtensionRepository extensionRepository;
    @Mock private PawnTransactionRepository pawnTxRepository;
    @Mock private CustomerService customerService;
    @Mock private ItemService itemService;
    @Mock private CashRegisterService cashRegisterService;
    @Mock private TransactionService transactionService;
    @Mock private SaleService saleService;
    @Mock private StaffService staffService;
    @Mock private NotificationService notificationService;

    @InjectMocks private PawnService pawnService;

    private static final long STAFF = 3L;
    private static final long SESSION = 77L;
    private static final long CONTRACT = 1L;

    // =====================================================================================
    @Nested
    @DisplayName("create()")
    class Create {

        private final PawnCreateRequest request = new PawnCreateRequest(
                5L,
                new ItemCreateRequest(ItemType.GOLD, ItemOriginType.PAWN, ItemStatus.IN_PAWN, "прстен", null),
                1000, 120, 30, LocalDate.of(2025, 1, 1), SESSION);

        @Test
        @DisplayName("saves a contract with the requested terms and disburses the principal as OUTFLOW")
        void create_savesContractAndDisburses() {
            Customer customer = mock(Customer.class);
            Item item = mock(Item.class);
            CashRegisterSession session = mock(CashRegisterSession.class);
            Transaction tx = mock(Transaction.class);
            when(itemService.create(request.item())).thenReturn(item);
            when(customerService.findOrFail(5L)).thenReturn(customer);
            when(cashRegisterService.requireOpenSession(SESSION)).thenReturn(session);
            when(transactionService.record(eq(STAFF), eq(session), eq(TransactionType.PAWN),
                    eq(new Money(1000)), eq(TransactionDirection.OUT), anyString())).thenReturn(tx);

            pawnService.create(request, STAFF);

            // ArgumentCaptor: assert the contract we persisted carries the requested terms.
            ArgumentCaptor<PawnContract> captor = ArgumentCaptor.forClass(PawnContract.class);
            verify(contractRepository).save(captor.capture());
            PawnContract saved = captor.getValue();
            assertSame(customer, saved.getCustomer());
            assertSame(item, saved.getItem());
            assertEquals(new Money(1000), saved.getPrincipalAmount());
            assertEquals(new Money(120), saved.getInterestAmount());
            assertEquals(30, saved.getTermDays());

            // InOrder: the contract is saved, then the cash transaction recorded, then applied.
            InOrder inOrder = inOrder(contractRepository, transactionService, cashRegisterService);
            inOrder.verify(contractRepository).save(any(PawnContract.class));
            inOrder.verify(transactionService).record(eq(STAFF), eq(session), eq(TransactionType.PAWN),
                    eq(new Money(1000)), eq(TransactionDirection.OUT), anyString());
            inOrder.verify(cashRegisterService).applyTransaction(tx);
            verify(pawnTxRepository).save(any(PawnTransaction.class));
        }
    }

    // =====================================================================================
    @Nested
    @DisplayName("extend()")
    class Extend {

        @Test
        @DisplayName("records interest + fee as a single INFLOW and saves the extension")
        void extend_recordsInflow() {
            PawnContract contract = mock(PawnContract.class);
            PawnContractExtension extension = mock(PawnContractExtension.class);
            CashRegisterSession session = mock(CashRegisterSession.class);
            Transaction tx = mock(Transaction.class);
            when(contractRepository.findById(CONTRACT)).thenReturn(Optional.of(contract));
            when(cashRegisterService.requireOpenSession(SESSION)).thenReturn(session);
            when(contract.extend(new Money(60), new Money(10))).thenReturn(extension);
            when(transactionService.record(eq(STAFF), eq(session), eq(TransactionType.PAWN),
                    eq(new Money(70)), eq(TransactionDirection.IN), anyString())).thenReturn(tx);

            PawnContractExtension result = pawnService.extend(CONTRACT, new PawnExtendRequest(60, 10, SESSION), STAFF);

            assertSame(extension, result);
            verify(extensionRepository).save(extension);
            verify(cashRegisterService).applyTransaction(tx);
        }

        @Test
        @DisplayName("treats a null fee as zero")
        void extend_nullFeeIsZero() {
            PawnContract contract = mock(PawnContract.class);
            when(contractRepository.findById(CONTRACT)).thenReturn(Optional.of(contract));
            when(cashRegisterService.requireOpenSession(SESSION)).thenReturn(mock(CashRegisterSession.class));
            when(contract.extend(new Money(60), new Money(0))).thenReturn(mock(PawnContractExtension.class));
            when(transactionService.record(any(), any(), any(), eq(new Money(60)), eq(TransactionDirection.IN), anyString()))
                    .thenReturn(mock(Transaction.class));

            pawnService.extend(CONTRACT, new PawnExtendRequest(60, null, SESSION), STAFF);

            verify(contract).extend(new Money(60), new Money(0));   // fee defaulted to 0, received = 60
        }
    }

    // =====================================================================================
    @Nested
    @DisplayName("redeem()")
    class Redeem {

        @Test
        @DisplayName("full payment: redeems, marks item redeemed, no manager notified")
        void redeem_fullPayment() {
            PawnContract contract = mock(PawnContract.class);
            Item item = mock(Item.class);
            when(contractRepository.findById(CONTRACT)).thenReturn(Optional.of(contract));
            when(cashRegisterService.requireOpenSession(SESSION)).thenReturn(mock(CashRegisterSession.class));
            when(contract.getItem()).thenReturn(item);
            when(contract.expectedRedemptionAmount()).thenReturn(new Money(1120));
            when(transactionService.record(any(), any(), any(), eq(new Money(1120)), eq(TransactionDirection.IN), anyString()))
                    .thenReturn(mock(Transaction.class));

            pawnService.redeem(CONTRACT, new PawnRedeemRequest(1120, SESSION), STAFF);

            verify(contract).redeem();
            verify(itemService).markRedeemed(item, STAFF);
            verifyNoInteractions(notificationService);
        }

        @Test
        @DisplayName("underpayment: manager receives a RISK_FLAG with the shortfall message")
        void redeem_underpayment_flagsManager() {
            PawnContract contract = mock(PawnContract.class);
            PawnTransaction pawnTx = mock(PawnTransaction.class);
            when(contractRepository.findById(CONTRACT)).thenReturn(Optional.of(contract));
            when(cashRegisterService.requireOpenSession(SESSION)).thenReturn(mock(CashRegisterSession.class));
            when(contract.getItem()).thenReturn(mock(Item.class));
            when(contract.getId()).thenReturn(CONTRACT);
            when(contract.expectedRedemptionAmount()).thenReturn(new Money(1120));
            when(transactionService.record(any(), any(), any(), any(), any(), anyString())).thenReturn(mock(Transaction.class));
            when(pawnTxRepository.save(any(PawnTransaction.class))).thenReturn(pawnTx);
            when(pawnTx.getId()).thenReturn(555L);
            when(staffService.findManagerId(STAFF)).thenReturn(Optional.of(9L));

            pawnService.redeem(CONTRACT, new PawnRedeemRequest(1000, SESSION), STAFF);

            // ArgumentCaptor on the free-text message to prove the shortfall is reported.
            ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
            verify(notificationService).create(eq(9L), eq(NotificationType.RISK_FLAG),
                    anyString(), message.capture(), eq("pawn_transaction"), eq(555L));
            assertTrue(message.getValue().contains("1000"));   // amount paid
            assertTrue(message.getValue().contains("1120"));   // amount expected
        }
    }

    // =====================================================================================
    @Nested
    @DisplayName("forfeit()")
    class Forfeit {

        @Test
        @DisplayName("forfeits the contract and lists the seized item for sale at the principal; no cash moves")
        void forfeit_listsItemForSale() {
            PawnContract contract = mock(PawnContract.class);
            Customer customer = mock(Customer.class);
            Item item = mock(Item.class);
            when(contractRepository.findById(CONTRACT)).thenReturn(Optional.of(contract));
            when(contract.getItem()).thenReturn(item);
            when(contract.getCustomer()).thenReturn(customer);
            when(contract.getPrincipalAmount()).thenReturn(new Money(1000));

            pawnService.forfeit(CONTRACT, STAFF);

            verify(contract).forfeit();
            verify(itemService).markInSale(item, STAFF);
            verify(saleService).createListing(customer, item, new Money(1000), STAFF);
            verifyNoInteractions(transactionService, cashRegisterService);
        }
    }

    // =====================================================================================
    @Nested
    @DisplayName("updateContract()")
    class UpdateContract {

        private PawnContract activeContractWithPrincipal(int principal) {
            PawnContract contract = mock(PawnContract.class);
            when(contractRepository.findById(CONTRACT)).thenReturn(Optional.of(contract));
            when(contract.isActive()).thenReturn(true);
            when(contract.getPrincipalAmount()).thenReturn(new Money(principal));
            return contract;
        }

        @Test
        @DisplayName("raising the principal pays the difference OUT")
        void update_increasePrincipal_movesCashOut() {
            PawnContract contract = activeContractWithPrincipal(1000);
            when(cashRegisterService.requireOpenSession(SESSION)).thenReturn(mock(CashRegisterSession.class));
            when(transactionService.record(eq(STAFF), any(), eq(TransactionType.PAWN),
                    eq(new Money(500)), eq(TransactionDirection.OUT), anyString())).thenReturn(mock(Transaction.class));
            when(staffService.findManagerId(STAFF)).thenReturn(Optional.empty());

            pawnService.updateContract(CONTRACT,
                    new PawnContractUpdateRequest(1500, 100, 30, LocalDate.of(2025, 1, 1), SESSION), STAFF);

            verify(transactionService).record(eq(STAFF), any(), eq(TransactionType.PAWN),
                    eq(new Money(500)), eq(TransactionDirection.OUT), anyString());
            verify(cashRegisterService).applyTransaction(any(Transaction.class));
            verify(contract).updateTerms(new Money(1500), new Money(100), 30, LocalDate.of(2025, 1, 1));
        }

        @Test
        @DisplayName("lowering the principal takes the difference back IN")
        void update_decreasePrincipal_movesCashIn() {
            activeContractWithPrincipal(1000);
            when(cashRegisterService.requireOpenSession(SESSION)).thenReturn(mock(CashRegisterSession.class));
            when(transactionService.record(eq(STAFF), any(), eq(TransactionType.PAWN),
                    eq(new Money(300)), eq(TransactionDirection.IN), anyString())).thenReturn(mock(Transaction.class));
            when(staffService.findManagerId(STAFF)).thenReturn(Optional.empty());

            pawnService.updateContract(CONTRACT,
                    new PawnContractUpdateRequest(700, 100, 30, LocalDate.of(2025, 1, 1), SESSION), STAFF);

            verify(transactionService).record(eq(STAFF), any(), eq(TransactionType.PAWN),
                    eq(new Money(300)), eq(TransactionDirection.IN), anyString());
        }

        @Test
        @DisplayName("unchanged principal moves no cash at all")
        void update_unchangedPrincipal_noCash() {
            PawnContract contract = activeContractWithPrincipal(1000);
            when(staffService.findManagerId(STAFF)).thenReturn(Optional.empty());

            pawnService.updateContract(CONTRACT,
                    new PawnContractUpdateRequest(1000, 200, 45, LocalDate.of(2025, 1, 1), null), STAFF);

            verify(contract).updateTerms(new Money(1000), new Money(200), 45, LocalDate.of(2025, 1, 1));
            verifyNoInteractions(transactionService, cashRegisterService, pawnTxRepository);
        }

        @Test
        @DisplayName("a non-active contract cannot be edited")
        void update_notActive_throws() {
            PawnContract contract = mock(PawnContract.class);
            when(contractRepository.findById(CONTRACT)).thenReturn(Optional.of(contract));
            when(contract.isActive()).thenReturn(false);

            assertThrows(BusinessRuleException.class, () -> pawnService.updateContract(CONTRACT,
                    new PawnContractUpdateRequest(1500, 100, 30, LocalDate.of(2025, 1, 1), SESSION), STAFF));
            verifyNoInteractions(transactionService, cashRegisterService);
        }

        @Test
        @DisplayName("changing the principal without a session id is rejected")
        void update_principalChangeWithoutSession_throws() {
            activeContractWithPrincipal(1000);

            assertThrows(BusinessRuleException.class, () -> pawnService.updateContract(CONTRACT,
                    new PawnContractUpdateRequest(1500, 100, 30, LocalDate.of(2025, 1, 1), null), STAFF));
            verify(cashRegisterService, never()).requireOpenSession(anyLong());
        }

        @Test
        @DisplayName("notifies the editor's manager that the contract was updated")
        void update_notifiesManager() {
            PawnContract contract = activeContractWithPrincipal(1000);
            when(contract.getId()).thenReturn(CONTRACT);
            when(staffService.findManagerId(STAFF)).thenReturn(Optional.of(9L));
            when(staffService.findStaffNames(Set.of(STAFF))).thenReturn(Map.of(STAFF, "Марко Марков"));

            pawnService.updateContract(CONTRACT,
                    new PawnContractUpdateRequest(1000, 100, 30, LocalDate.of(2025, 1, 1), null), STAFF);

            verify(notificationService).create(eq(9L), eq(NotificationType.PAWN_UPDATED),
                    anyString(), anyString(), eq("pawn_contract"), eq(CONTRACT));
        }
    }

    // =====================================================================================
    @Nested
    @DisplayName("get() / list()")
    class Queries {

        @Test
        @DisplayName("get returns the contract when it exists")
        void get_found() {
            PawnContract contract = mock(PawnContract.class);
            when(contractRepository.findById(CONTRACT)).thenReturn(Optional.of(contract));

            assertSame(contract, pawnService.get(CONTRACT));
        }

        @Test
        @DisplayName("get throws ResourceNotFoundException when it does not exist")
        void get_notFound() {
            when(contractRepository.findById(CONTRACT)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> pawnService.get(CONTRACT));
        }

        @Test
        @DisplayName("filtering by opening staff restricts the query to the caller's own team")
        void list_scopesToVisibleStaff() {
            when(staffService.findSubordinateStaffIds(STAFF)).thenReturn(List.of(20L, 21L));
            when(contractRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(Page.<PawnContract>empty());

            PawnFilterRequest filter = PawnFilterRequest.builder().createdByStaffId(20L).build();
            pawnService.list(filter, PageRequest.of(0, 10), STAFF);

            // The team (subordinates + caller) is resolved before the query is scoped to it.
            verify(staffService).findSubordinateStaffIds(STAFF);
            verify(contractRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }
}
