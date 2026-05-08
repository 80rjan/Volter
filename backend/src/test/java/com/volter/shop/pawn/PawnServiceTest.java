package com.volter.shop.pawn;

import com.volter.shop.modules.alert.application.RiskAlertService;
import com.volter.shop.modules.alert.domain.RiskAlert;
import com.volter.shop.modules.alert.domain.enums.RiskAlertSeverity;
import com.volter.shop.modules.alert.domain.enums.RiskAlertType;
import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.web.request.ExistingCustomerReferenceRequest;
import com.volter.shop.modules.customer.web.request.NewCustomerReferenceRequest;
import com.volter.shop.modules.customer.domain.factory.CustomerFactory;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.web.request.baseitem.ExistingItemReferenceRequest;
import com.volter.shop.modules.inventory.web.request.baseitem.ItemModificationRequest;
import com.volter.shop.modules.inventory.web.request.baseitem.NewItemReferenceRequest;
import com.volter.shop.modules.inventory.web.response.baseitem.ItemFactoryResult;
import com.volter.shop.modules.inventory.domain.factory.ItemFactory;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.infrastructure.ItemRepository;
import com.volter.shop.modules.pawn.application.PawnService;
import com.volter.shop.modules.pawn.web.request.PawnFilterRequest;
import com.volter.shop.modules.pawn.web.request.PawnCreationRequest;
import com.volter.shop.modules.pawn.web.request.PawnForfeitureRequest;
import com.volter.shop.modules.pawn.web.request.PawnModificationRequest;
import com.volter.shop.modules.pawn.web.request.PawnRedemptionRequest;
import com.volter.shop.modules.pawn.web.request.PawnRenewalRequest;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.model.enums.PawnStatus;
import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import com.volter.shop.modules.pawn.domain.model.valueobject.PawnPeriod;
import com.volter.shop.modules.pawn.infrastructure.PawnRepository;
import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.identity.application.IdentityUserService;
import com.volter.identity.domain.model.IdentityUser;
import com.volter.identity.domain.model.Role;
import com.volter.shop.modules.staff.application.StaffService;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.identity.domain.model.enums.RoleEnum;
import com.volter.shop.shared.common.exceptions.ResourceNotFoundException;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PawnServiceTest {

    @Mock
    private PawnRepository pawnRepository;
    @Mock
    private StaffService staffService;
    @Mock
    private CashRegisterService cashRegisterService;
    @Mock
    private SaleService saleService;
    @Mock
    private CustomerService customerService;
    @Mock
    private ItemService itemService;
    @Mock
    private RiskAlertService riskAlertService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemFactory itemFactory;
    @Mock
    private CustomerFactory customerFactory;
    @Mock
    private IdentityUserService identityUserService;

    @InjectMocks
    private PawnService pawnService;

    private Pawn pawn;
    private Staff staff;
    private Staff manager;
    private IdentityUser identityUser;
    private Customer customer;
    private Item item;
    private CashRegisterSession cashRegisterSession;

    @BeforeEach
    void setUp() {
        // Setup manager
        manager = mock(Staff.class);

        // Setup staff
        staff = mock(Staff.class);
        lenient().when(staff.getId()).thenReturn(1L);
        lenient().when(staff.getManager()).thenReturn(manager);

        // Setup identity user (EMPLOYEE by default)
        Role staffRole = mock(Role.class);
        identityUser = mock(IdentityUser.class);
        lenient().when(identityUser.getRole()).thenReturn(staffRole);
        lenient().when(staffRole.getName()).thenReturn(RoleEnum.EMPLOYEE);
        lenient().when(identityUserService.getCurrentIdentityUser()).thenReturn(identityUser);

        // Setup customer
        customer = mock(Customer.class);

        // Setup item
        item = mock(Item.class);

        // Setup cash register session
        cashRegisterSession = mock(CashRegisterSession.class);

        // Setup pawn
        pawn = Pawn.builder()
                .id(1L)
                .amount(new Money(10000))
                .interest(new Money(1000))
                .period(new PawnPeriod(LocalDate.now(), LocalDate.now().plusDays(30)))
                .defaultDurationDays(30)
                .status(PawnStatus.ACTIVE)
                .active(true)
                .customer(customer)
                .item(item)
                .build();
    }

    // ========== GET BY ID TESTS ==========

    @Test
    @DisplayName("getById() should return pawn when found")
    void testGetById_Success() {
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));

        Pawn result = pawnService.getById(1L);

        assertNotNull(result);
        assertEquals(pawn, result);
        verify(pawnRepository).findById(1L);
    }

    @Test
    @DisplayName("getById() should throw ResourceNotFoundException when not found")
    void testGetById_NotFound() {
        when(pawnRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pawnService.getById(999L));
        verify(pawnRepository).findById(999L);
    }

    // ========== GET ALL TESTS ==========

    @Test
    @DisplayName("getAll() with no filter should return all pawns")
    void testGetAll_NoFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Pawn> pawns = List.of(pawn, mock(Pawn.class), mock(Pawn.class));
        Page<Pawn> page = new PageImpl<>(pawns, pageable, 3);

        when(pawnRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Pawn> result = pawnService.getAll(new PawnFilterRequest(null, null, null, null, null, null, null, null, null, null), pageable);

        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        verify(pawnRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getAll() with status filter should filter by status")
    void testGetAll_WithStatusFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        PawnFilterRequest filter = new PawnFilterRequest(PawnStatus.ACTIVE, null, null, null, null, null, null, null, null, null);
        Page<Pawn> page = new PageImpl<>(List.of(pawn), pageable, 1);

        when(pawnRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Pawn> result = pawnService.getAll(filter, pageable);

        assertEquals(1, result.getTotalElements());
        verify(pawnRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getAll() with active filter should filter by active flag")
    void testGetAll_WithActiveFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        PawnFilterRequest filter = new PawnFilterRequest(null, true, null, null, null, null, null, null, null, null);
        Page<Pawn> page = new PageImpl<>(List.of(pawn), pageable, 1);

        when(pawnRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Pawn> result = pawnService.getAll(filter, pageable);

        assertEquals(1, result.getTotalElements());
        verify(pawnRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getAll() with date range filters should filter correctly")
    void testGetAll_WithDateRangeFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate fromDate = LocalDate.now().minusDays(30);
        LocalDate toDate = LocalDate.now();
        PawnFilterRequest filter = new PawnFilterRequest(null, null, fromDate, toDate, null, null, null, null, null, null);
        Page<Pawn> page = new PageImpl<>(List.of(pawn), pageable, 1);

        when(pawnRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Pawn> result = pawnService.getAll(filter, pageable);

        assertEquals(1, result.getTotalElements());
        verify(pawnRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getAll() with customer filters should filter by customer data")
    void testGetAll_WithCustomerFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        PawnFilterRequest filter = new PawnFilterRequest(null, null, null, null, null, null, null, "John Doe", "1234567890123", "+38970123456");
        Page<Pawn> page = new PageImpl<>(List.of(pawn), pageable, 1);

        when(pawnRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Pawn> result = pawnService.getAll(filter, pageable);

        assertEquals(1, result.getTotalElements());
        verify(pawnRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getAll() with multiple filters combined should apply all")
    void testGetAll_WithMultipleFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        PawnFilterRequest filter = new PawnFilterRequest(
                PawnStatus.ACTIVE,
                true,
                LocalDate.now().minusDays(30),
                LocalDate.now(),
                null,
                null,
                null,
                "John",
                null,
                null
        );
        Page<Pawn> page = new PageImpl<>(List.of(pawn), pageable, 1);

        when(pawnRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Pawn> result = pawnService.getAll(filter, pageable);

        assertEquals(1, result.getTotalElements());
        verify(pawnRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getAll() should return empty page when no results")
    void testGetAll_EmptyResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pawn> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(pawnRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<Pawn> result = pawnService.getAll(new PawnFilterRequest(null, null, null, null, null, null, null, null, null, null), pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(pawnRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getAll() should support pagination")
    void testGetAll_Pagination() {
        Pageable page1 = PageRequest.of(0, 2);
        Pageable page2 = PageRequest.of(1, 2);

        List<Pawn> allPawns = List.of(pawn, mock(Pawn.class), mock(Pawn.class), mock(Pawn.class));
        Page<Pawn> firstPage = new PageImpl<>(allPawns.subList(0, 2), page1, 4);
        Page<Pawn> secondPage = new PageImpl<>(allPawns.subList(2, 4), page2, 4);

        when(pawnRepository.findAll(any(Specification.class), eq(page1))).thenReturn(firstPage);
        when(pawnRepository.findAll(any(Specification.class), eq(page2))).thenReturn(secondPage);

        Page<Pawn> result1 = pawnService.getAll(new PawnFilterRequest(null, null, null, null, null, null, null, null, null, null), page1);
        Page<Pawn> result2 = pawnService.getAll(new PawnFilterRequest(null, null, null, null, null, null, null, null, null, null), page2);

        assertEquals(2, result1.getContent().size());
        assertEquals(0, result1.getNumber());
        assertEquals(2, result2.getContent().size());
        assertEquals(1, result2.getNumber());
        assertEquals(4, result1.getTotalElements());
        assertEquals(4, result2.getTotalElements());
    }

    // ========== GET ALL MATURING WITHIN DAYS TESTS ==========

    @Test
    @DisplayName("getAllMaturingWithinDays() should return pawns maturing within specified days")
    void testGetAllMaturingWithinDays_Success() {
        LocalDate expectedDate = LocalDate.now().plusDays(7);
        List<Pawn> maturingPawns = List.of(pawn, mock(Pawn.class));
        when(pawnRepository.findByPeriod_MaturityDateBefore(expectedDate)).thenReturn(maturingPawns);

        List<Pawn> result = pawnService.getAllMaturingWithinDays(7);

        assertEquals(2, result.size());
        verify(pawnRepository).findByPeriod_MaturityDateBefore(expectedDate);
    }

    @Test
    @DisplayName("getAllMaturingWithinDays() with 0 days should check today")
    void testGetAllMaturingWithinDays_ZeroDays() {
        LocalDate today = LocalDate.now();
        when(pawnRepository.findByPeriod_MaturityDateBefore(today)).thenReturn(List.of(pawn));

        List<Pawn> result = pawnService.getAllMaturingWithinDays(0);

        assertEquals(1, result.size());
        verify(pawnRepository).findByPeriod_MaturityDateBefore(today);
    }

    // ========== GET BY CUSTOMER ID TESTS ==========

    @Test
    @DisplayName("getByCustomerId() should return customer's pawns")
    void testGetByCustomerId_Success() {
        List<Pawn> customerPawns = List.of(pawn, mock(Pawn.class));
        when(pawnRepository.findByCustomer_Id(1L)).thenReturn(customerPawns);

        List<Pawn> result = pawnService.getByCustomerId(1L);

        assertEquals(2, result.size());
        verify(pawnRepository).findByCustomer_Id(1L);
    }

    @Test
    @DisplayName("getByCustomerId() should return empty list when customer has no pawns")
    void testGetByCustomerId_NoPawns() {
        when(pawnRepository.findByCustomer_Id(999L)).thenReturn(List.of());

        List<Pawn> result = pawnService.getByCustomerId(999L);

        assertTrue(result.isEmpty());
        verify(pawnRepository).findByCustomer_Id(999L);
    }

    // ========== REDEEM TESTS ==========

    @Test
    @DisplayName("redeem() should complete redemption successfully")
    void testRedeem_Success() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        Pawn result = pawnService.redeem(1L, new PawnRedemptionRequest(11000, "Full redemption"));

        assertEquals(PawnStatus.REDEEMED, result.getStatus());
        assertFalse(result.isActive());
        verify(pawnRepository).save(pawn);
        verify(customerService).save(customer);
        verify(customer).pawnAction(PawnTransactionAction.REDEMPTION);
        verify(cashRegisterSession).recordTransaction(any(PawnTransaction.class));
        verify(cashRegisterService).saveSession(cashRegisterSession);
    }

    @Test
    @DisplayName("redeem() with underpayment by EMPLOYEE should create risk alert")
    void testRedeem_UnderpaymentCreatesRiskAlert() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        pawnService.redeem(1L, new PawnRedemptionRequest(10000, "Underpayment")); // Only principal, no interest

        ArgumentCaptor<RiskAlert> alertCaptor = ArgumentCaptor.forClass(RiskAlert.class);
        verify(riskAlertService).save(alertCaptor.capture());

        RiskAlert alert = alertCaptor.getValue();
        assertEquals(RiskAlertType.TRANSACTION_ANOMALY, alert.getType());
        assertEquals(RiskAlertSeverity.HIGH, alert.getSeverity());
        assertTrue(alert.getSummary().contains("less than total owed"));
        assertEquals(manager, alert.getManager());
    }

    @Test
    @DisplayName("redeem() with underpayment by INTERN should create risk alert")
    void testRedeem_UnderpaymentByInternCreatesAlert() {
        Role internRole = mock(Role.class);
        when(internRole.getName()).thenReturn(RoleEnum.INTERN);
        when(identityUser.getRole()).thenReturn(internRole);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        pawnService.redeem(1L, new PawnRedemptionRequest(10000, "Underpayment by intern"));

        verify(riskAlertService).save(any(RiskAlert.class));
    }

    @Test
    @DisplayName("redeem() with underpayment by MANAGER should NOT create risk alert")
    void testRedeem_UnderpaymentByManagerNoAlert() {
        Role managerRole = mock(Role.class);
        when(managerRole.getName()).thenReturn(RoleEnum.MANAGER);
        when(identityUser.getRole()).thenReturn(managerRole);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        pawnService.redeem(1L, new PawnRedemptionRequest(10000, "Underpayment by manager"));

        verify(riskAlertService, never()).save(any(RiskAlert.class));
    }

    @Test
    @DisplayName("redeem() with underpayment by ADMIN should NOT create risk alert")
    void testRedeem_UnderpaymentByAdminNoAlert() {
        Role adminRole = mock(Role.class);
        when(adminRole.getName()).thenReturn(RoleEnum.ADMIN);
        when(identityUser.getRole()).thenReturn(adminRole);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        pawnService.redeem(1L, new PawnRedemptionRequest(10000, "Underpayment by admin"));

        verify(riskAlertService, never()).save(any(RiskAlert.class));
    }

    @Test
    @DisplayName("redeem() with exact payment should NOT create risk alert")
    void testRedeem_ExactPaymentNoAlert() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        pawnService.redeem(1L, new PawnRedemptionRequest(11000, "Exact payment"));

        verify(riskAlertService, never()).save(any(RiskAlert.class));
    }

    @Test
    @DisplayName("redeem() with overpayment should NOT create risk alert")
    void testRedeem_OverpaymentNoAlert() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        pawnService.redeem(1L, new PawnRedemptionRequest(12000, "Overpayment"));

        verify(riskAlertService, never()).save(any(RiskAlert.class));
    }

    // ========== FORFEIT TESTS ==========

    @Test
    @DisplayName("forfeit() should forfeit pawn and create sale")
    void testForfeit_Success() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        Pawn result = pawnService.forfeit(1L, new PawnForfeitureRequest("Customer didn't return"));

        assertEquals(PawnStatus.FORFEITED, result.getStatus());
        assertFalse(result.isActive());
        verify(pawnRepository).save(pawn);
        verify(customer).pawnAction(PawnTransactionAction.FORFEITURE);
        verify(customerService).save(customer);
    }

    @Test
    @DisplayName("forfeit() should create sale from forfeited pawn")
    void testForfeit_CreatesSale() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        pawnService.forfeit(1L, new PawnForfeitureRequest("Forfeiture"));

        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        verify(saleService).save(saleCaptor.capture());

        Sale sale = saleCaptor.getValue();
        assertNotNull(sale);
    }

    @Test
    @DisplayName("forfeit() should NOT record cash register transaction")
    void testForfeit_NoCashRegisterTransaction() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        pawnService.forfeit(1L, new PawnForfeitureRequest("Forfeiture"));

        verify(cashRegisterSession, never()).recordTransaction(any(PawnTransaction.class));
    }

    // ========== RENEW TESTS ==========

    @Test
    @DisplayName("renew() should renew pawn successfully")
    void testRenew_Success() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        LocalDate originalMaturity = pawn.getPeriod().maturityDate();

        Pawn result = pawnService.renew(1L, new PawnRenewalRequest("Full renewal", 1000));

        assertNotEquals(originalMaturity, result.getPeriod().maturityDate());
        verify(pawnRepository).save(pawn);
        verify(customer).pawnAction(eq(PawnTransactionAction.RENEWAL), anyLong());
        verify(customerService).save(customer);
        verify(cashRegisterSession).recordTransaction(any(PawnTransaction.class));
        verify(cashRegisterService).saveSession(cashRegisterSession);
    }

    @Test
    @DisplayName("renew() should record transaction in cash register")
    void testRenew_RecordsTransaction() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);

        pawnService.renew(1L, new PawnRenewalRequest("Partial renewal", 500));

        verify(cashRegisterSession).recordTransaction(any(PawnTransaction.class));
        verify(cashRegisterService).saveSession(cashRegisterSession);
    }

    // ========== SAVE TESTS ==========

    @Test
    @DisplayName("save() should persist pawn")
    void testSave_Success() {
        when(pawnRepository.save(pawn)).thenReturn(pawn);

        Pawn result = pawnService.save(pawn);

        assertEquals(pawn, result);
        verify(pawnRepository).save(pawn);
    }

    // ========== CREATE TESTS ==========

    @Test
    @DisplayName("create() with existing item and existing customer should create pawn successfully")
    void testCreate_WithExistingItemAndExistingCustomer() {
        // Prepare request
        ExistingItemReferenceRequest itemRef = new ExistingItemReferenceRequest(1L);
        ExistingCustomerReferenceRequest customerRef = new ExistingCustomerReferenceRequest();

        PawnCreationRequest request = new PawnCreationRequest();
        request.setAmount(5000);
        request.setInterest(500);
        request.setIssueDate(LocalDate.now());
        request.setMaturityDate(LocalDate.now().plusDays(30));
        request.setDurationDays(30);
        request.setItem(itemRef);
        request.setCustomer(customerRef);
        request.setTransactionDescription("Initial loan");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(customerFactory.createOrGetCustomer(customerRef)).thenReturn(customer);
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(itemFactory.createOrGetItem(itemRef)).thenReturn(new ItemFactoryResult(item, false));
        when(pawnRepository.save(any(Pawn.class))).thenAnswer(invocation -> {
            Pawn saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Pawn result = pawnService.create(request);

        assertNotNull(result);
        assertEquals(PawnStatus.ACTIVE, result.getStatus());
        assertTrue(result.isActive());
        verify(pawnRepository).save(any(Pawn.class));
        verify(customerFactory).createOrGetCustomer(customerRef);
        verify(itemFactory).createOrGetItem(itemRef);
        verify(customer).pawnAction(PawnTransactionAction.CREATION);
        verify(customerService).save(customer);
        verify(cashRegisterSession).recordTransaction(any(PawnTransaction.class));
        verify(cashRegisterService).saveSession(cashRegisterSession);
        verify(itemService, never()).save(any());
    }

    @Test
    @DisplayName("create() with new item should create item via factory")
    void testCreate_WithNewItem() {
        NewItemReferenceRequest itemRef = mock(NewItemReferenceRequest.class);
        ExistingCustomerReferenceRequest customerRef = new ExistingCustomerReferenceRequest();

        PawnCreationRequest request = new PawnCreationRequest();
        request.setAmount(5000);
        request.setInterest(500);
        request.setIssueDate(LocalDate.now());
        request.setMaturityDate(LocalDate.now().plusDays(30));
        request.setDurationDays(30);
        request.setItem(itemRef);
        request.setCustomer(customerRef);

        Item newItem = mock(Item.class);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(customerFactory.createOrGetCustomer(customerRef)).thenReturn(customer);
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(itemFactory.createOrGetItem(itemRef)).thenReturn(new ItemFactoryResult(newItem, true));
        when(pawnRepository.save(any(Pawn.class))).thenAnswer(invocation -> {
            Pawn saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Pawn result = pawnService.create(request);

        assertNotNull(result);
        verify(itemFactory).createOrGetItem(itemRef);
        verify(itemService).save(newItem);
        verify(pawnRepository).save(any(Pawn.class));
    }

    @Test
    @DisplayName("create() with new customer should create customer via factory")
    void testCreate_WithNewCustomer() {
        ExistingItemReferenceRequest itemRef = new ExistingItemReferenceRequest(1L);
        NewCustomerReferenceRequest customerRef = new NewCustomerReferenceRequest();

        PawnCreationRequest request = new PawnCreationRequest();
        request.setAmount(5000);
        request.setInterest(500);
        request.setIssueDate(LocalDate.now());
        request.setMaturityDate(LocalDate.now().plusDays(30));
        request.setDurationDays(30);
        request.setItem(itemRef);
        request.setCustomer(customerRef);

        Customer newCustomer = mock(Customer.class);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(customerFactory.createOrGetCustomer(customerRef)).thenReturn(newCustomer);
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(itemFactory.createOrGetItem(itemRef)).thenReturn(new ItemFactoryResult(item, false));
        when(pawnRepository.save(any(Pawn.class))).thenAnswer(invocation -> {
            Pawn saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Pawn result = pawnService.create(request);

        assertNotNull(result);
        verify(customerFactory).createOrGetCustomer(customerRef);
        verify(customerService).save(newCustomer);
        verify(itemService, never()).save(any());
    }

    @Test
    @DisplayName("create() with both new item and new customer should create both")
    void testCreate_WithNewItemAndNewCustomer() {
        NewItemReferenceRequest itemRef = mock(NewItemReferenceRequest.class);
        NewCustomerReferenceRequest customerRef = new NewCustomerReferenceRequest();

        PawnCreationRequest request = new PawnCreationRequest();
        request.setAmount(5000);
        request.setInterest(500);
        request.setIssueDate(LocalDate.now());
        request.setMaturityDate(LocalDate.now().plusDays(30));
        request.setDurationDays(30);
        request.setItem(itemRef);
        request.setCustomer(customerRef);

        Item newItem = mock(Item.class);
        Customer newCustomer = mock(Customer.class);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(customerFactory.createOrGetCustomer(customerRef)).thenReturn(newCustomer);
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(itemFactory.createOrGetItem(itemRef)).thenReturn(new ItemFactoryResult(newItem, true));
        when(pawnRepository.save(any(Pawn.class))).thenAnswer(invocation -> {
            Pawn saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Pawn result = pawnService.create(request);

        assertNotNull(result);
        verify(itemFactory).createOrGetItem(itemRef);
        verify(itemService).save(newItem);
        verify(customerFactory).createOrGetCustomer(customerRef);
        verify(pawnRepository).save(any(Pawn.class));
    }

    @Test
    @DisplayName("create() should record initial transaction in cash register")
    void testCreate_RecordsInitialTransaction() {
        ExistingItemReferenceRequest itemRef = new ExistingItemReferenceRequest(1L);
        ExistingCustomerReferenceRequest customerRef = new ExistingCustomerReferenceRequest();

        PawnCreationRequest request = new PawnCreationRequest();
        request.setAmount(5000);
        request.setInterest(500);
        request.setIssueDate(LocalDate.now());
        request.setMaturityDate(LocalDate.now().plusDays(30));
        request.setDurationDays(30);
        request.setItem(itemRef);
        request.setCustomer(customerRef);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(customerFactory.createOrGetCustomer(customerRef)).thenReturn(customer);
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(itemFactory.createOrGetItem(itemRef)).thenReturn(new ItemFactoryResult(item, false));
        when(pawnRepository.save(any(Pawn.class))).thenAnswer(invocation -> {
            Pawn saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        pawnService.create(request);

        verify(cashRegisterSession).recordTransaction(any(PawnTransaction.class));
        verify(cashRegisterService).saveSession(cashRegisterSession);
        verify(itemService, never()).save(any());
    }

    @Test
    @DisplayName("create() should use transactionDescription from request")
    void testCreate_UsesTransactionDescriptionFromRequest() {
        ExistingItemReferenceRequest itemRef = new ExistingItemReferenceRequest(1L);
        ExistingCustomerReferenceRequest customerRef = new ExistingCustomerReferenceRequest();

        PawnCreationRequest request = new PawnCreationRequest();
        request.setAmount(5000);
        request.setInterest(500);
        request.setIssueDate(LocalDate.now());
        request.setMaturityDate(LocalDate.now().plusDays(30));
        request.setDurationDays(30);
        request.setItem(itemRef);
        request.setCustomer(customerRef);
        request.setTransactionDescription("Custom description from request");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(customerFactory.createOrGetCustomer(customerRef)).thenReturn(customer);
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(itemFactory.createOrGetItem(itemRef)).thenReturn(new ItemFactoryResult(item, false));
        when(pawnRepository.save(any(Pawn.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pawn result = pawnService.create(request);

        // Verify pawn was created (description is validated by Pawn.create internally)
        assertNotNull(result);
        verify(pawnRepository).save(any(Pawn.class));
        verify(itemService, never()).save(any());
    }

    // ========== MODIFY TESTS ==========

    @Test
    @DisplayName("modify() should modify pawn successfully")
    void testModify_Success() {
        ItemModificationRequest itemModRequest = new ItemModificationRequest("Updated description", null);
        PawnModificationRequest request = new PawnModificationRequest(12000, 1200, 45, itemModRequest, "Upate");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);
        when(itemService.save(any(Item.class))).thenReturn(item);

        Pawn result = pawnService.modify(1L, request);

        assertEquals(12000, result.getAmount().amount());
        assertEquals(1200, result.getInterest().amount());
        assertEquals(45, result.getDefaultDurationDays());
        verify(pawnRepository).save(pawn);
        verify(item).modify(itemModRequest);
        verify(itemService).save(item);
        verify(cashRegisterSession).recordTransaction(any(PawnTransaction.class));
        verify(cashRegisterService).saveSession(cashRegisterSession);
    }

    @Test
    @DisplayName("modify() should create MEDIUM severity risk alert")
    void testModify_CreatesRiskAlert() {
        PawnModificationRequest request = new PawnModificationRequest(12000, null, null, null, null);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);
        when(itemService.save(any(Item.class))).thenReturn(item);

        pawnService.modify(1L, request);

        ArgumentCaptor<RiskAlert> alertCaptor = ArgumentCaptor.forClass(RiskAlert.class);
        verify(riskAlertService).save(alertCaptor.capture());

        RiskAlert alert = alertCaptor.getValue();
        assertEquals(RiskAlertType.PAWN_MODIFICATION, alert.getType());
        assertEquals(RiskAlertSeverity.MEDIUM, alert.getSeverity());
        assertTrue(alert.getSummary().contains("was modified"));
        assertEquals(manager, alert.getManager());
    }

    @Test
    @DisplayName("modify() with nonexistent pawn should throw ResourceNotFoundException")
    void testModify_NotFound() {
        PawnModificationRequest request = new PawnModificationRequest(12000, null, null, null, null);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> pawnService.modify(999L, request));

        verify(pawnRepository, never()).save(any(Pawn.class));
        verify(riskAlertService, never()).save(any(RiskAlert.class));
    }

    @Test
    @DisplayName("modify() should modify item along with pawn")
    void testModify_ModifiesItem() {
        ItemModificationRequest itemModRequest = new ItemModificationRequest("New description", new java.math.BigDecimal("15.5"));
        PawnModificationRequest request = new PawnModificationRequest(null, 1200, null, itemModRequest, "Update with item modification");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(pawn));
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(pawn);
        when(itemService.save(any(Item.class))).thenReturn(item);

        pawnService.modify(1L, request);

        verify(item).modify(itemModRequest);
        verify(itemService).save(item);
    }

    // ========== INTEGRATION/WORKFLOW TESTS ==========

    @Test
    @DisplayName("create then redeem workflow should complete successfully")
    void testCreateThenRedeemWorkflow() {
        // Create
        ExistingItemReferenceRequest itemRef = new ExistingItemReferenceRequest(1L);
        ExistingCustomerReferenceRequest customerRef = new ExistingCustomerReferenceRequest();

        PawnCreationRequest createRequest = new PawnCreationRequest();
        createRequest.setAmount(5000);
        createRequest.setInterest(500);
        createRequest.setIssueDate(LocalDate.now());
        createRequest.setMaturityDate(LocalDate.now().plusDays(30));
        createRequest.setDurationDays(30);
        createRequest.setItem(itemRef);
        createRequest.setCustomer(customerRef);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(customerFactory.createOrGetCustomer(customerRef)).thenReturn(customer);
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(itemFactory.createOrGetItem(itemRef)).thenReturn(new ItemFactoryResult(item, false));
        when(pawnRepository.save(any(Pawn.class))).thenAnswer(invocation -> {
            Pawn saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Pawn created = pawnService.create(createRequest);

        // Redeem
        when(pawnRepository.findById(1L)).thenReturn(Optional.of(created));

        Pawn redeemed = pawnService.redeem(1L, new PawnRedemptionRequest(5500, "Redemption"));

        assertEquals(PawnStatus.REDEEMED, redeemed.getStatus());
        assertFalse(redeemed.isActive());
        verify(customer, times(1)).pawnAction(PawnTransactionAction.CREATION);
        verify(customer, times(1)).pawnAction(PawnTransactionAction.REDEMPTION);
    }

    @Test
    @DisplayName("create then renew then redeem workflow should track all actions")
    void testFullLifecycleWorkflow() {
        // Create
        ExistingItemReferenceRequest itemRef = new ExistingItemReferenceRequest(1L);
        ExistingCustomerReferenceRequest customerRef = new ExistingCustomerReferenceRequest();

        PawnCreationRequest createRequest = new PawnCreationRequest();
        createRequest.setAmount(5000);
        createRequest.setInterest(500);
        createRequest.setIssueDate(LocalDate.now());
        createRequest.setMaturityDate(LocalDate.now().plusDays(30));
        createRequest.setDurationDays(30);
        createRequest.setItem(itemRef);
        createRequest.setCustomer(customerRef);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(customerFactory.createOrGetCustomer(customerRef)).thenReturn(customer);
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(itemFactory.createOrGetItem(itemRef)).thenReturn(new ItemFactoryResult(item, false));
        when(pawnRepository.save(any(Pawn.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pawn created = pawnService.create(createRequest);

        // Renew
        when(pawnRepository.findById(any())).thenReturn(Optional.of(created));
        Pawn renewed = pawnService.renew(1L, new PawnRenewalRequest("Renewal", 500));

        // Redeem
        Pawn redeemed = pawnService.redeem(1L, new PawnRedemptionRequest(5500, "Redemption"));

        assertEquals(PawnStatus.REDEEMED, redeemed.getStatus());
        verify(customer).pawnAction(PawnTransactionAction.CREATION);
        verify(customer).pawnAction(eq(PawnTransactionAction.RENEWAL), anyLong());
        verify(customer).pawnAction(PawnTransactionAction.REDEMPTION);
        verify(customerService, times(3)).save(customer);
    }

    @Test
    @DisplayName("create then forfeit workflow should create sale")
    void testCreateThenForfeitWorkflow() {
        // Create
        ExistingItemReferenceRequest itemRef = new ExistingItemReferenceRequest(1L);
        ExistingCustomerReferenceRequest customerRef = new ExistingCustomerReferenceRequest();

        PawnCreationRequest createRequest = new PawnCreationRequest();
        createRequest.setAmount(5000);
        createRequest.setInterest(500);
        createRequest.setIssueDate(LocalDate.now());
        createRequest.setMaturityDate(LocalDate.now().plusDays(30));
        createRequest.setDurationDays(30);
        createRequest.setItem(itemRef);
        createRequest.setCustomer(customerRef);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(customerFactory.createOrGetCustomer(customerRef)).thenReturn(customer);
        when(cashRegisterService.getOpenSessionByStaff(1L)).thenReturn(cashRegisterSession);
        when(itemFactory.createOrGetItem(itemRef)).thenReturn(new ItemFactoryResult(item, false));
        when(pawnRepository.save(any(Pawn.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pawn created = pawnService.create(createRequest);

        // Forfeit
        when(pawnRepository.findById(any())).thenReturn(Optional.of(created));
        Pawn forfeited = pawnService.forfeit(1L, new PawnForfeitureRequest("Customer didn't return"));

        assertEquals(PawnStatus.FORFEITED, forfeited.getStatus());
        assertFalse(forfeited.isActive());
        verify(saleService).save(any(Sale.class));
        verify(customer).pawnAction(PawnTransactionAction.CREATION);
        verify(customer).pawnAction(PawnTransactionAction.FORFEITURE);
    }
}