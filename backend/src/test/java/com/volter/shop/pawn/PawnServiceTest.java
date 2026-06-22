package com.volter.shop.pawn;

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
import com.volter.shop.modules.pawn.application.PawnService;
import com.volter.shop.modules.pawn.application.dto.PawnCreateRequest;
import com.volter.shop.modules.pawn.application.dto.PawnExtendRequest;
import com.volter.shop.modules.pawn.application.dto.PawnRedeemRequest;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.PawnContractExtension;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.repository.PawnContractExtensionRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.identity.modules.staff.application.StaffService;
import com.volter.platform.modules.notification.application.NotificationService;
import com.volter.platform.modules.notification.domain.model.enums.NotificationType;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PawnServiceTest {

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

    @InjectMocks
    private PawnService pawnService;

    @Nested
    @DisplayName("create()")
    class Create {

        private final PawnCreateRequest request = new PawnCreateRequest(
                5L,
                new ItemCreateRequest(ItemType.GOLD, ItemOriginType.PAWN, ItemStatus.IN_PAWN, "ring", null),
                1000, 120, 30, LocalDate.of(2025, 1, 1), 77L);

        private Customer customer;
        private Item item;
        private CashRegisterSession session;
        private Transaction tx;

        private void stubHappyPath() {
            customer = mock(Customer.class);
            item = mock(Item.class);
            session = mock(CashRegisterSession.class);
            tx = mock(Transaction.class);
            when(itemService.create(request.item())).thenReturn(item);
            when(customerService.findOrFail(5L)).thenReturn(customer);
            when(cashRegisterService.requireOpenSession(77L)).thenReturn(session);
            when(transactionService.record(eq(3L), eq(session), eq(TransactionType.PAWN),
                    eq(new Money(1000)), eq(TransactionDirection.OUT), anyString())).thenReturn(tx);
        }

        @Test
        @DisplayName("creates the item inline and saves a contract with the principal and interest")
        void savesContract() {
            stubHappyPath();

            pawnService.create(request, 3L);

            verify(itemService).create(request.item());
            ArgumentCaptor<PawnContract> captor = ArgumentCaptor.forClass(PawnContract.class);
            verify(contractRepository).save(captor.capture());
            PawnContract saved = captor.getValue();
            assertSame(customer, saved.getCustomer());
            assertSame(item, saved.getItem());
            assertEquals(new Money(1000), saved.getPrincipalAmount());
            assertEquals(new Money(120), saved.getInterestAmount());
            assertEquals(30, saved.getTermDays());
        }

        @Test
        @DisplayName("disburses the principal as a cash OUTFLOW and applies it to the session")
        void disbursesPrincipal() {
            stubHappyPath();

            pawnService.create(request, 3L);

            verify(transactionService).record(eq(3L), eq(session), eq(TransactionType.PAWN),
                    eq(new Money(1000)), eq(TransactionDirection.OUT), anyString());
            verify(cashRegisterService).applyTransaction(tx);
            verify(pawnTxRepository).save(any(PawnTransaction.class));
        }
    }

    @Nested
    @DisplayName("extend()")
    class Extend {

        @Test
        @DisplayName("extends the contract and records interest + fee as a cash INFLOW")
        void extendsAndRecordsInflow() {
            PawnContract contract = mock(PawnContract.class);
            PawnContractExtension extension = mock(PawnContractExtension.class);
            CashRegisterSession session = mock(CashRegisterSession.class);
            Transaction tx = mock(Transaction.class);
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(cashRegisterService.requireOpenSession(77L)).thenReturn(session);
            when(contract.extend(new Money(60), new Money(10))).thenReturn(extension);
            when(transactionService.record(eq(3L), eq(session), eq(TransactionType.PAWN),
                    eq(new Money(70)), eq(TransactionDirection.IN), anyString())).thenReturn(tx);

            PawnContractExtension result = pawnService.extend(1L, new PawnExtendRequest(60, 10, 77L), 3L);

            assertSame(extension, result);
            verify(extensionRepository).save(extension);
            verify(cashRegisterService).applyTransaction(tx);
            verify(pawnTxRepository).save(any(PawnTransaction.class));
        }

        @Test
        @DisplayName("treats a null fee as zero")
        void nullFee_treatedAsZero() {
            PawnContract contract = mock(PawnContract.class);
            CashRegisterSession session = mock(CashRegisterSession.class);
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(cashRegisterService.requireOpenSession(77L)).thenReturn(session);
            when(contract.extend(new Money(60), new Money(0))).thenReturn(mock(PawnContractExtension.class));
            when(transactionService.record(eq(3L), eq(session), eq(TransactionType.PAWN),
                    eq(new Money(60)), eq(TransactionDirection.IN), anyString())).thenReturn(mock(Transaction.class));

            pawnService.extend(1L, new PawnExtendRequest(60, null, 77L), 3L);

            verify(contract).extend(new Money(60), new Money(0));
        }
    }

    @Nested
    @DisplayName("redeem()")
    class Redeem {

        @Test
        @DisplayName("redeems the contract, marks the item redeemed and takes the staff-entered amount as INFLOW")
        void redeems() {
            PawnContract contract = mock(PawnContract.class);
            Item item = mock(Item.class);
            CashRegisterSession session = mock(CashRegisterSession.class);
            Transaction tx = mock(Transaction.class);
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(cashRegisterService.requireOpenSession(77L)).thenReturn(session);
            when(contract.getItem()).thenReturn(item);
            when(contract.expectedRedemptionAmount()).thenReturn(new Money(1120));
            when(transactionService.record(eq(3L), eq(session), eq(TransactionType.PAWN),
                    eq(new Money(1120)), eq(TransactionDirection.IN), anyString())).thenReturn(tx);

            // Paid exactly what was expected -> no risk flag raised.
            pawnService.redeem(1L, new PawnRedeemRequest(1120, 77L), 3L);

            verify(contract).redeem();
            verify(itemService).markRedeemed(item, 3L);
            verify(cashRegisterService).applyTransaction(tx);
            verify(pawnTxRepository).save(any(PawnTransaction.class));
            verifyNoInteractions(notificationService);
        }

        @Test
        @DisplayName("flags the staff member's manager when the redemption is underpaid")
        void underpaidRaisesRiskFlag() {
            PawnContract contract = mock(PawnContract.class);
            Item item = mock(Item.class);
            CashRegisterSession session = mock(CashRegisterSession.class);
            Transaction tx = mock(Transaction.class);
            PawnTransaction pawnTx = mock(PawnTransaction.class);
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(cashRegisterService.requireOpenSession(77L)).thenReturn(session);
            when(contract.getItem()).thenReturn(item);
            when(contract.getId()).thenReturn(1L);
            when(contract.expectedRedemptionAmount()).thenReturn(new Money(1120));
            when(transactionService.record(any(), any(), any(), any(), any(), anyString())).thenReturn(tx);
            when(pawnTxRepository.save(any(PawnTransaction.class))).thenReturn(pawnTx);
            when(pawnTx.getId()).thenReturn(555L);
            when(staffService.findManagerId(3L)).thenReturn(Optional.of(9L));

            // Paid less than expected -> manager gets a RISK_FLAG pointing at the pawn transaction.
            pawnService.redeem(1L, new PawnRedeemRequest(1000, 77L), 3L);

            verify(notificationService).create(eq(9L), eq(NotificationType.RISK_FLAG),
                    anyString(), anyString(), eq("pawn_transaction"), eq(555L));
        }
    }

    @Nested
    @DisplayName("forfeit()")
    class Forfeit {

        @Test
        @DisplayName("forfeits the contract and lists the seized item for sale at the unrecovered principal")
        void forfeitsAndLists() {
            PawnContract contract = mock(PawnContract.class);
            Customer customer = mock(Customer.class);
            Item item = mock(Item.class);
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(contract.getItem()).thenReturn(item);
            when(contract.getCustomer()).thenReturn(customer);
            when(contract.getPrincipalAmount()).thenReturn(new Money(1000));

            pawnService.forfeit(1L, 3L);

            verify(contract).forfeit();
            verify(itemService).markInSale(item, 3L);
            verify(saleService).createListing(customer, item, new Money(1000), 3L);
            verifyNoInteractions(transactionService);
        }
    }
}
