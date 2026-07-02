package com.volter.shop.sale;

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
import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.sale.application.dto.SaleCreateRequest;
import com.volter.shop.modules.sale.application.dto.SaleSellRequest;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.domain.model.SaleTransaction;
import com.volter.shop.modules.sale.domain.repository.SaleRepository;
import com.volter.shop.modules.sale.domain.repository.SaleTransactionRepository;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.notification.application.NotificationService;
import com.volter.shop.modules.notification.domain.model.enums.NotificationType;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock private SaleRepository saleRepository;
    @Mock private SaleTransactionRepository saleTxRepository;
    @Mock private CustomerService customerService;
    @Mock private ItemService itemService;
    @Mock private CashRegisterService cashRegisterService;
    @Mock private TransactionService transactionService;
    @Mock private StaffService staffService;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private SaleService saleService;

    @Nested
    @DisplayName("createListing(request)")
    class CreateListingFromRequest {

        private final SaleCreateRequest request = new SaleCreateRequest(
                5L,
                new ItemCreateRequest(ItemType.GOLD, ItemOriginType.PURCHASE, ItemStatus.IN_SALE, "chain", null),
                900, 77L);

        private Customer customer;
        private Item item;
        private CashRegisterSession session;
        private Transaction tx;

        private void stubHappyPath() {
            customer = mock(Customer.class);
            item = mock(Item.class);
            session = mock(CashRegisterSession.class);
            tx = mock(Transaction.class);
            when(customerService.findOrFail(5L)).thenReturn(customer);
            when(cashRegisterService.requireOpenSession(77L)).thenReturn(session);
            when(itemService.create(request.item())).thenReturn(item);
            when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));
            when(transactionService.record(eq(3L), eq(session), eq(TransactionType.SALE),
                    eq(new Money(900)), eq(TransactionDirection.OUT), anyString())).thenReturn(tx);
        }

        @Test
        @DisplayName("creates the item inline and saves a sale listing at the purchase price")
        void createsItemAndSale() {
            stubHappyPath();

            saleService.createListing(request, 3L);

            verify(itemService).create(request.item());
            ArgumentCaptor<Sale> captor = ArgumentCaptor.forClass(Sale.class);
            verify(saleRepository).save(captor.capture());
            assertEquals(new Money(900), captor.getValue().getPurchasePrice());
            assertSame(item, captor.getValue().getItem());
        }

        @Test
        @DisplayName("buying an item for resale records a cash OUTFLOW and applies it")
        void recordsOutflow() {
            stubHappyPath();

            saleService.createListing(request, 3L);

            verify(transactionService).record(eq(3L), eq(session), eq(TransactionType.SALE),
                    eq(new Money(900)), eq(TransactionDirection.OUT), anyString());
            verify(cashRegisterService).applyTransaction(tx);
            verify(saleTxRepository).save(any(SaleTransaction.class));
        }
    }

    @Nested
    @DisplayName("createListing(entity) — used by pawn forfeit")
    class CreateListingFromEntity {

        @Test
        @DisplayName("saves a listing for an already-owned item without any cash movement")
        void savesListingNoCash() {
            Customer customer = mock(Customer.class);
            Item item = mock(Item.class);
            when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

            saleService.createListing(customer, item, new Money(1200), 3L);

            ArgumentCaptor<Sale> captor = ArgumentCaptor.forClass(Sale.class);
            verify(saleRepository).save(captor.capture());
            assertEquals(new Money(1200), captor.getValue().getPurchasePrice());
            verifyNoInteractions(transactionService);
            verify(cashRegisterService, never()).applyTransaction(any());
        }
    }

    @Nested
    @DisplayName("sell()")
    class Sell {

        @Test
        @DisplayName("sells the item, records an INFLOW and marks the item SOLD")
        void sells() {
            Sale sale = mock(Sale.class);
            Item item = mock(Item.class);
            CashRegisterSession session = mock(CashRegisterSession.class);
            Transaction tx = mock(Transaction.class);
            when(saleRepository.findById(10L)).thenReturn(Optional.of(sale));
            when(sale.getItem()).thenReturn(item);
            when(cashRegisterService.requireOpenSession(77L)).thenReturn(session);
            when(transactionService.record(eq(3L), eq(session), eq(TransactionType.SALE),
                    eq(new Money(1500)), eq(TransactionDirection.IN), anyString())).thenReturn(tx);

            saleService.sell(10L, new SaleSellRequest(1500, 77L), 3L);

            verify(sale).sell(new Money(1500));
            verify(itemService).markSold(item, 3L);
            verify(cashRegisterService).applyTransaction(tx);
            verify(saleTxRepository).save(any(SaleTransaction.class));
            verifyNoInteractions(notificationService);
        }

        @Test
        @DisplayName("flags the staff member's manager when sold below purchase price")
        void underpricedRaisesRiskFlag() {
            Sale sale = mock(Sale.class);
            Item item = mock(Item.class);
            CashRegisterSession session = mock(CashRegisterSession.class);
            Transaction tx = mock(Transaction.class);
            SaleTransaction saleTx = mock(SaleTransaction.class);
            when(saleRepository.findById(10L)).thenReturn(Optional.of(sale));
            when(sale.getItem()).thenReturn(item);
            when(sale.getId()).thenReturn(10L);
            when(sale.isUnderwater()).thenReturn(true);
            when(sale.getSalePrice()).thenReturn(new Money(800));
            when(sale.getPurchasePrice()).thenReturn(new Money(1000));
            when(cashRegisterService.requireOpenSession(77L)).thenReturn(session);
            when(transactionService.record(any(), any(), any(), any(), any(), anyString())).thenReturn(tx);
            when(saleTxRepository.save(any(SaleTransaction.class))).thenReturn(saleTx);
            when(saleTx.getId()).thenReturn(321L);
            when(staffService.findManagerId(3L)).thenReturn(Optional.of(9L));

            saleService.sell(10L, new SaleSellRequest(800, 77L), 3L);

            verify(notificationService).create(eq(9L), eq(NotificationType.RISK_FLAG),
                    anyString(), anyString(), eq("sale_transaction"), eq(321L));
        }
    }

    @Nested
    @DisplayName("cancel()")
    class Cancel {

        @Test
        @DisplayName("cancels the listing")
        void cancels() {
            Sale sale = mock(Sale.class);
            when(saleRepository.findById(10L)).thenReturn(Optional.of(sale));

            saleService.cancel(10L);

            verify(sale).cancel();
        }
    }
}
