package com.volter.shop.sale;

import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.application.dto.request.ExistingCustomerReferenceRequest;
import com.volter.shop.modules.customer.application.dto.request.NewCustomerReferenceRequest;
import com.volter.shop.modules.customer.domain.factory.CustomerFactory;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.application.dtos.baseitem.request.ExistingItemReferenceRequest;
import com.volter.shop.modules.inventory.application.dtos.baseitem.request.NewItemReferenceRequest;
import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemFactoryResult;
import com.volter.shop.modules.inventory.domain.factory.ItemFactory;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.sale.application.dto.filter.SaleFilter;
import com.volter.shop.modules.sale.application.dto.request.SaleCreationRequest;
import com.volter.shop.modules.sale.application.dto.request.SaleSellRequest;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.domain.model.SaleTransaction;
import com.volter.shop.modules.sale.domain.model.enums.SaleStatus;
import com.volter.shop.modules.sale.domain.repository.SaleRepository;
import com.volter.shop.modules.sale.infrastructure.SaleMapper;
import com.volter.shop.modules.staff.application.StaffService;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.shared.common.exceptions.ResourceNotFoundException;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock private StaffService staffService;
    @Mock private SaleRepository saleRepository;
    @Mock private CashRegisterService cashRegisterService;
    @Mock private SaleMapper saleMapper;
    @Mock private TransactionService transactionService;
    @Mock private ItemService itemService;
    @Mock private CustomerService customerService;
    @Mock private CustomerFactory customerFactory;
    @Mock private ItemFactory itemFactory;

    @InjectMocks
    private SaleService saleService;

    private Sale sale;
    private Staff staff;
    private Customer customer;
    private Item item;
    private CashRegisterSession cashRegisterSession;

    @BeforeEach
    void setUp() {
        staff = mock(Staff.class);
        lenient().when(staff.getId()).thenReturn(1L);

        customer = mock(Customer.class);
        item = mock(Item.class);
        cashRegisterSession = mock(CashRegisterSession.class);

        sale = Sale.builder()
                .id(1L)
                .purchasePrice(new Money(5000))
                .status(SaleStatus.LISTED)
                .active(true)
                .customer(customer)
                .item(item)
                .build();
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private SaleCreationRequest creationRequest(int price,
                                                Object itemRef,
                                                Object customerRef) {
        return SaleCreationRequest.builder()
                .purchasePrice(price)
                .item((com.volter.shop.modules.inventory.application.dtos.baseitem.request.ItemReferenceRequest) itemRef)
                .customer((com.volter.shop.modules.customer.application.dto.request.CustomerReferenceRequest) customerRef)
                .transactionDescription("desc")
                .build();
    }

    private void stubCreate(ItemFactoryResult itemFactoryResult, Customer c) {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(customerFactory.createOrGetCustomer(any())).thenReturn(c);
        when(itemFactory.createOrGetItem(any())).thenReturn(itemFactoryResult);
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // =========================================================================
    // save()
    // =========================================================================

    @Test
    @DisplayName("save() persists and returns sale")
    void save_success() {
        when(saleRepository.save(sale)).thenReturn(sale);
        assertEquals(sale, saleService.save(sale));
        verify(saleRepository).save(sale);
    }

    // =========================================================================
    // getAll() — pagination + filters
    // =========================================================================

    @Nested
    @DisplayName("getAll() with filters and pagination")
    class GetAll {

        @Test
        @DisplayName("returns page from repository")
        void returnsPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Sale> page = new PageImpl<>(List.of(sale), pageable, 1);
            SaleFilter filter = new SaleFilter(null, null, null, null, null, null);

            when(saleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Sale> result = saleService.getAll(filter, pageable);

            assertEquals(1, result.getTotalElements());
            verify(saleRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("empty filter returns all sales")
        void emptyFilter_returnsAll() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Sale> page = new PageImpl<>(List.of(sale, mock(Sale.class)));
            SaleFilter filter = new SaleFilter(null, null, null, null, null, null);

            when(saleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Sale> result = saleService.getAll(filter, pageable);

            assertEquals(2, result.getContent().size());
        }

        @Test
        @DisplayName("status filter is passed to specification")
        void statusFilter_passedToSpec() {
            Pageable pageable = PageRequest.of(0, 10);
            SaleFilter filter = new SaleFilter(SaleStatus.LISTED, null, null, null, null, null);
            Page<Sale> page = new PageImpl<>(List.of(sale));

            when(saleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Sale> result = saleService.getAll(filter, pageable);

            assertEquals(1, result.getContent().size());
            verify(saleRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("active filter is passed to specification")
        void activeFilter_passedToSpec() {
            Pageable pageable = PageRequest.of(0, 10);
            SaleFilter filter = new SaleFilter(null, true, null, null, null, null);
            Page<Sale> page = new PageImpl<>(List.of(sale));

            when(saleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Sale> result = saleService.getAll(filter, pageable);

            assertNotNull(result);
            verify(saleRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("itemType filter is passed to specification")
        void itemTypeFilter_passedToSpec() {
            Pageable pageable = PageRequest.of(0, 10);
            SaleFilter filter = new SaleFilter(null, null, ItemType.GOLD, null, null, null);
            Page<Sale> page = new PageImpl<>(List.of(sale));

            when(saleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Sale> result = saleService.getAll(filter, pageable);

            assertNotNull(result);
            verify(saleRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("customerName filter is passed to specification")
        void customerNameFilter_passedToSpec() {
            Pageable pageable = PageRequest.of(0, 10);
            SaleFilter filter = new SaleFilter(null, null, null, "Test IdentityUser", null, null);
            Page<Sale> page = new PageImpl<>(List.of(sale));

            when(saleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Sale> result = saleService.getAll(filter, pageable);

            assertNotNull(result);
            verify(saleRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("combined filters are all passed to specification")
        void combinedFilters_passedToSpec() {
            Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
            SaleFilter filter = new SaleFilter(SaleStatus.LISTED, true, ItemType.ELECTRONIC, "Jane", null, null);
            Page<Sale> page = new PageImpl<>(List.of());

            when(saleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Sale> result = saleService.getAll(filter, pageable);

            assertTrue(result.getContent().isEmpty());
            verify(saleRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("second page returns correct offset")
        void pagination_secondPage() {
            Pageable pageable = PageRequest.of(1, 5);
            SaleFilter filter = new SaleFilter(null, null, null, null, null, null);
            Page<Sale> page = new PageImpl<>(List.of(sale), pageable, 10);

            when(saleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Sale> result = saleService.getAll(filter, pageable);

            assertEquals(1, result.getNumber());
            assertEquals(10, result.getTotalElements());
            assertEquals(2, result.getTotalPages());
        }

        @Test
        @DisplayName("empty result page returns empty content")
        void emptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            SaleFilter filter = new SaleFilter(SaleStatus.SOLD, null, null, null, null, null);
            Page<Sale> page = Page.empty(pageable);

            when(saleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Sale> result = saleService.getAll(filter, pageable);

            assertTrue(result.isEmpty());
        }
    }

    // =========================================================================
    // getByCustomerId()
    // =========================================================================

    @Nested
    @DisplayName("getByCustomerId()")
    class GetByCustomerId {

        @Test
        void returnsCustomerSales() {
            when(saleRepository.findByCustomer_Id(1L)).thenReturn(List.of(sale, mock(Sale.class)));
            assertEquals(2, saleService.getByCustomerId(1L).size());
        }

        @Test
        void returnsEmptyList_whenNoSales() {
            when(saleRepository.findByCustomer_Id(999L)).thenReturn(List.of());
            assertTrue(saleService.getByCustomerId(999L).isEmpty());
        }
    }

    // =========================================================================
    // getById()
    // =========================================================================

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        void returnsSale_whenFound() {
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
            assertEquals(sale, saleService.getById(1L));
        }

        @Test
        void throws_whenNotFound() {
            when(saleRepository.findById(999L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> saleService.getById(999L));
        }
    }

    // =========================================================================
    // sell()
    // =========================================================================

    @Nested
    @DisplayName("sell()")
    class Sell {

        private void stubSell(long saleId) {
            when(staffService.getCurrentStaff()).thenReturn(staff);
            when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
            when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
            when(saleRepository.save(any(Sale.class))).thenReturn(sale);
        }

        @Test
        void atProfit_statusIsSold() {
            stubSell(1L);
            Sale result = saleService.sell(1L, new SaleSellRequest(7000, "profit"));
            assertEquals(SaleStatus.SOLD, result.getStatus());
            assertEquals(7000, result.getSoldPrice().amount());
        }

        @Test
        void atBreakEven_statusIsSold() {
            stubSell(1L);
            Sale result = saleService.sell(1L, new SaleSellRequest(5000, "even"));
            assertEquals(SaleStatus.SOLD, result.getStatus());
        }

        @Test
        void atLoss_statusIsSold() {
            stubSell(1L);
            Sale result = saleService.sell(1L, new SaleSellRequest(3000, "loss"));
            assertEquals(SaleStatus.SOLD, result.getStatus());
            assertEquals(3000, result.getSoldPrice().amount());
        }

        @Test
        void recordsTransactionInCashRegister() {
            stubSell(1L);
            saleService.sell(1L, new SaleSellRequest(6000, "desc"));
            verify(cashRegisterSession).recordTransaction(any(SaleTransaction.class));
            verify(cashRegisterService).saveSession(cashRegisterSession);
        }

        @Test
        void preservesOriginalPurchasePrice() {
            stubSell(1L);
            Sale result = saleService.sell(1L, new SaleSellRequest(7000, "desc"));
            assertEquals(5000, result.getPurchasePrice().amount());
        }

        @Test
        void zeroSoldPrice_allowed() {
            stubSell(1L);
            Sale result = saleService.sell(1L, new SaleSellRequest(0, "free"));
            assertEquals(0, result.getSoldPrice().amount());
        }

        @Test
        void throws_whenSaleNotFound() {
            when(staffService.getCurrentStaff()).thenReturn(staff);
            when(saleRepository.findById(999L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> saleService.sell(999L, new SaleSellRequest(6000, "desc")));
            verify(saleRepository, never()).save(any());
        }

        @Test
        void throws_whenAlreadySold() {
            sale.setStatus(SaleStatus.SOLD);
            sale.setSoldPrice(new Money(6000));
            when(staffService.getCurrentStaff()).thenReturn(staff);
            when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
            when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);

            assertThrows(IllegalStateException.class,
                    () -> saleService.sell(1L, new SaleSellRequest(7000, "desc")));
            verify(saleRepository, never()).save(any());
        }
    }

    // =========================================================================
    // create()
    // =========================================================================

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        void existingItemAndCustomer_createsSale() {
            var itemRef = new ExistingItemReferenceRequest(1L);
            var customerRef = new ExistingCustomerReferenceRequest();
            stubCreate(new ItemFactoryResult(item, false), customer);

            Sale result = saleService.create(creationRequest(5000, itemRef, customerRef));

            assertNotNull(result);
            assertEquals(SaleStatus.LISTED, result.getStatus());
            assertTrue(result.isActive());
            assertEquals(5000, result.getPurchasePrice().amount());
            verify(saleRepository).save(any(Sale.class));
            verify(itemService, never()).save(any());
        }

        @Test
        void newItem_savesItem() {
            Item newItem = mock(Item.class);
            stubCreate(new ItemFactoryResult(newItem, true), customer);

            saleService.create(creationRequest(5000,
                    mock(NewItemReferenceRequest.class),
                    new ExistingCustomerReferenceRequest()));

            verify(itemService).save(newItem);
        }

        @Test
        void existingItem_doesNotSaveItem() {
            stubCreate(new ItemFactoryResult(item, false), customer);

            saleService.create(creationRequest(5000,
                    new ExistingItemReferenceRequest(1L),
                    new ExistingCustomerReferenceRequest()));

            verify(itemService, never()).save(any());
        }

        @Test
        void newCustomer_customerFactoryCreatesIt() {
            Customer newCustomer = mock(Customer.class);
            stubCreate(new ItemFactoryResult(item, false), newCustomer);

            saleService.create(creationRequest(5000,
                    new ExistingItemReferenceRequest(1L),
                    new NewCustomerReferenceRequest()));

            verify(customerFactory).createOrGetCustomer(any());
            verify(customerService).save(newCustomer);
        }

        @Test
        void incrementsCustomerSaleCount() {
            stubCreate(new ItemFactoryResult(item, false), customer);

            saleService.create(creationRequest(5000,
                    new ExistingItemReferenceRequest(1L),
                    new ExistingCustomerReferenceRequest()));

            verify(customer).saleAction();
            verify(customerService).save(customer);
        }

        @Test
        void recordsInitialTransactionInCashRegister() {
            stubCreate(new ItemFactoryResult(item, false), customer);

            saleService.create(creationRequest(5000,
                    new ExistingItemReferenceRequest(1L),
                    new ExistingCustomerReferenceRequest()));

            verify(cashRegisterSession).recordTransaction(any(SaleTransaction.class));
            verify(cashRegisterService).saveSession(cashRegisterSession);
        }

        @Test
        void zeroPurchasePrice_allowed() {
            stubCreate(new ItemFactoryResult(item, false), customer);

            Sale result = saleService.create(creationRequest(0,
                    new ExistingItemReferenceRequest(1L),
                    new ExistingCustomerReferenceRequest()));

            assertEquals(0, result.getPurchasePrice().amount());
        }

        @Test
        void newItemAndNewCustomer_savesBoth() {
            Item newItem = mock(Item.class);
            Customer newCustomer = mock(Customer.class);
            stubCreate(new ItemFactoryResult(newItem, true), newCustomer);

            saleService.create(creationRequest(5000,
                    mock(NewItemReferenceRequest.class),
                    new NewCustomerReferenceRequest()));

            verify(itemService).save(newItem);
            verify(customerService).save(newCustomer);
        }
    }

    // =========================================================================
    // workflow: create → sell
    // =========================================================================

    @Test
    @DisplayName("create then sell workflow completes successfully")
    void workflow_createThenSell() {
        stubCreate(new ItemFactoryResult(item, false), customer);

        Sale created = saleService.create(creationRequest(5000,
                new ExistingItemReferenceRequest(1L),
                new ExistingCustomerReferenceRequest()));

        when(saleRepository.findById(any())).thenReturn(Optional.of(created));
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

        Sale sold = saleService.sell(1L, new SaleSellRequest(7000, "sold"));

        assertEquals(SaleStatus.SOLD, sold.getStatus());
        assertEquals(7000, sold.getSoldPrice().amount());
        verify(customer).saleAction();
        // create records 1 transaction, sell records 1 more
        verify(cashRegisterSession, times(2)).recordTransaction(any(SaleTransaction.class));
    }
}
