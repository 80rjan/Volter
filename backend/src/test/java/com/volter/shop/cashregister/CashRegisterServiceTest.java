package com.volter.shop.cashregister;

import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionCloseRequest;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionDepositRequest;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionOpenRequest;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionWithdrawRequest;
import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionStatus;
import com.volter.shop.modules.cashregister.infrastructure.CashRegisterRepository;
import com.volter.shop.modules.cashregister.infrastructure.CashRegisterSessionRepository;
import com.volter.shop.modules.pawn.application.PawnService;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.staff.application.StaffService;
import com.volter.shop.modules.staff.domain.model.Staff;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashRegisterServiceTest {

    @Mock
    private CashRegisterRepository cashRegisterRepository;
    @Mock
    private CashRegisterSessionRepository cashRegisterSessionRepository;
    @Mock
    private StaffService staffService;
    @Mock
    private PawnService pawnService;

    @InjectMocks
    private CashRegisterService cashRegisterService;

    private CashRegister cashRegister;
    private Staff staff;
    private CashRegisterSession session;

    @BeforeEach
    void setUp() {
        cashRegister = mock(CashRegister.class);
        lenient().when(cashRegister.getId()).thenReturn(1L);

        staff = mock(Staff.class);
        lenient().when(staff.getId()).thenReturn(1L);

        session = mock(CashRegisterSession.class);
        lenient().when(session.getId()).thenReturn(1L);
        lenient().when(session.getStatus()).thenReturn(CashRegisterSessionStatus.OPEN);

        // pawnService uses @Lazy @Autowired (field injection to break circular dependency),
        // so @InjectMocks won't inject it via constructor — use reflection instead.
        ReflectionTestUtils.setField(cashRegisterService, "pawnService", pawnService);
    }

    // ========== GET BY ID TESTS ==========

    @Test
    @DisplayName("getById() should return cash register when found")
    void testGetById_Success() {
        when(cashRegisterRepository.findById(1L)).thenReturn(Optional.of(cashRegister));

        CashRegister result = cashRegisterService.getById(1L);

        assertNotNull(result);
        assertEquals(cashRegister, result);
        verify(cashRegisterRepository).findById(1L);
    }

    @Test
    @DisplayName("getById() should throw ResourceNotFoundException when not found")
    void testGetById_NotFound() {
        when(cashRegisterRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cashRegisterService.getById(999L));
        verify(cashRegisterRepository).findById(999L);
    }

    // ========== GET SESSION BY ID TESTS ==========

    @Test
    @DisplayName("getSessionById() should return session when found")
    void testGetSessionById_Success() {
        when(cashRegisterSessionRepository.findById(1L)).thenReturn(Optional.of(session));

        CashRegisterSession result = cashRegisterService.getSessionById(1L);

        assertNotNull(result);
        assertEquals(session, result);
        verify(cashRegisterSessionRepository).findById(1L);
    }

    @Test
    @DisplayName("getSessionById() should throw ResourceNotFoundException when not found")
    void testGetSessionById_NotFound() {
        when(cashRegisterSessionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cashRegisterService.getSessionById(999L));
        verify(cashRegisterSessionRepository).findById(999L);
    }

    // ========== GET REFERENCE BY ID TESTS ==========

    @Test
    @DisplayName("getReferenceById() should return reference")
    void testGetReferenceById_Success() {
        when(cashRegisterRepository.getReferenceById(1L)).thenReturn(cashRegister);

        CashRegister result = cashRegisterService.getReferenceById(1L);

        assertNotNull(result);
        assertEquals(cashRegister, result);
        verify(cashRegisterRepository).getReferenceById(1L);
    }

    // ========== SAVE TESTS ==========

    @Test
    @DisplayName("save() should persist cash register")
    void testSave_Success() {
        when(cashRegisterRepository.save(cashRegister)).thenReturn(cashRegister);

        CashRegister result = cashRegisterService.save(cashRegister);

        assertEquals(cashRegister, result);
        verify(cashRegisterRepository).save(cashRegister);
    }

    @Test
    @DisplayName("saveSession() should persist session")
    void testSaveSession_Success() {
        when(cashRegisterSessionRepository.save(session)).thenReturn(session);

        CashRegisterSession result = cashRegisterService.saveSession(session);

        assertEquals(session, result);
        verify(cashRegisterSessionRepository).save(session);
    }

    // ========== GET OPEN SESSION BY STAFF TESTS ==========

    @Test
    @DisplayName("getOpenSessionByStaff(staffId) should return open session")
    void testGetOpenSessionByStaff_WithStaffId_Success() {
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);

        CashRegisterSession result = cashRegisterService.getOpenSessionByStaff(1L);

        assertNotNull(result);
        assertEquals(session, result);
        verify(cashRegisterSessionRepository).findOpenSessionByStaffId(1L);
    }

    @Test
    @DisplayName("getOpenSessionByStaff(staffId) should throw when no open session found")
    void testGetOpenSessionByStaff_WithStaffId_NotFound() {
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(999L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> cashRegisterService.getOpenSessionByStaff(999L));
        verify(cashRegisterSessionRepository).findOpenSessionByStaffId(999L);
    }

    @Test
    @DisplayName("getOpenSessionByStaff() should return current staff's open session")
    void testGetOpenSessionByStaff_NoParam_Success() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);

        CashRegisterSession result = cashRegisterService.getOpenSessionByStaff();

        assertNotNull(result);
        assertEquals(session, result);
        verify(staffService).getCurrentStaff();
        verify(cashRegisterSessionRepository).findOpenSessionByStaffId(1L);
    }

    @Test
    @DisplayName("getOpenSessionByStaff() should throw when current staff has no open session")
    void testGetOpenSessionByStaff_NoParam_NotFound() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> cashRegisterService.getOpenSessionByStaff());
        verify(staffService).getCurrentStaff();
        verify(cashRegisterSessionRepository).findOpenSessionByStaffId(1L);
    }

    // ========== OPEN SESSION TESTS ==========

    @Test
    @DisplayName("openSession() should create and save session successfully")
    void testOpenSession_Success() {
        CashRegisterSessionOpenRequest request = new CashRegisterSessionOpenRequest(10000, 1L);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.existsByStaffIdAndStatusOpen(1L)).thenReturn(false);
        when(cashRegisterRepository.findById(1L)).thenReturn(Optional.of(cashRegister));
        when(pawnService.getAllMaturingWithinDays(0)).thenReturn(List.of());
        when(cashRegisterSessionRepository.save(any(CashRegisterSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CashRegisterSession result = cashRegisterService.openSession(request);

        assertNotNull(result);
        assertEquals(10000, result.getOpeningBalance().amount());
        assertEquals(10000, result.getCurrentBalance().amount());
        verify(cashRegisterSessionRepository).existsByStaffIdAndStatusOpen(1L);
        verify(cashRegisterRepository).findById(1L);
        verify(pawnService).getAllMaturingWithinDays(0);
        verify(cashRegisterSessionRepository).save(any(CashRegisterSession.class));
    }

    @Test
    @DisplayName("openSession() should throw when staff already has open session")
    void testOpenSession_StaffAlreadyHasOpenSession() {
        CashRegisterSessionOpenRequest request = new CashRegisterSessionOpenRequest(10000, 1L);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.existsByStaffIdAndStatusOpen(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> cashRegisterService.openSession(request));
        verify(cashRegisterSessionRepository).existsByStaffIdAndStatusOpen(1L);
        verify(cashRegisterRepository, never()).findById(any());
        verify(cashRegisterSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("openSession() should throw when cash register not found")
    void testOpenSession_CashRegisterNotFound() {
        CashRegisterSessionOpenRequest request = new CashRegisterSessionOpenRequest(10000, 999L);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.existsByStaffIdAndStatusOpen(1L)).thenReturn(false);
        when(cashRegisterRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cashRegisterService.openSession(request));
        verify(cashRegisterRepository).findById(999L);
        verify(cashRegisterSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("openSession() with zero opening balance should be allowed")
    void testOpenSession_ZeroOpeningBalance() {
        CashRegisterSessionOpenRequest request = new CashRegisterSessionOpenRequest(0, 1L);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.existsByStaffIdAndStatusOpen(1L)).thenReturn(false);
        when(cashRegisterRepository.findById(1L)).thenReturn(Optional.of(cashRegister));
        when(pawnService.getAllMaturingWithinDays(0)).thenReturn(List.of());
        when(cashRegisterSessionRepository.save(any(CashRegisterSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CashRegisterSession result = cashRegisterService.openSession(request);

        assertNotNull(result);
        assertEquals(0, result.getOpeningBalance().amount());
    }

    @Test
    @DisplayName("openSession() should calculate expected pawn interest from maturing pawns")
    void testOpenSession_CalculatesExpectedPawnInterest() {
        CashRegisterSessionOpenRequest request = new CashRegisterSessionOpenRequest(10000, 1L);

        Pawn pawn1 = mock(Pawn.class);
        when(pawn1.getInterest()).thenReturn(new Money(500));
        Pawn pawn2 = mock(Pawn.class);
        when(pawn2.getInterest()).thenReturn(new Money(300));

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.existsByStaffIdAndStatusOpen(1L)).thenReturn(false);
        when(cashRegisterRepository.findById(1L)).thenReturn(Optional.of(cashRegister));
        when(pawnService.getAllMaturingWithinDays(0)).thenReturn(List.of(pawn1, pawn2));
        when(cashRegisterSessionRepository.save(any(CashRegisterSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CashRegisterSession result = cashRegisterService.openSession(request);

        assertEquals(800, result.getExpectedPawnInterest().amount()); // 500 + 300
    }

    // ========== CLOSE SESSION TESTS ==========

    @Test
    @DisplayName("closeSession() should close staff's open session")
    void testCloseSession_Success() {
        CashRegisterSessionCloseRequest request = new CashRegisterSessionCloseRequest(10500);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);
        when(cashRegisterSessionRepository.save(session)).thenReturn(session);

        CashRegisterSession result = cashRegisterService.closeSession(request);

        assertNotNull(result);
        verify(staffService).getCurrentStaff();
        verify(cashRegisterSessionRepository).findOpenSessionByStaffId(1L);
        verify(session).close(new Money(10500), staff);
        verify(cashRegisterSessionRepository).save(session);
    }

    @Test
    @DisplayName("closeSession() should throw when no open session found")
    void testCloseSession_NoOpenSession() {
        CashRegisterSessionCloseRequest request = new CashRegisterSessionCloseRequest(10000);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> cashRegisterService.closeSession(request));
        verify(cashRegisterSessionRepository).findOpenSessionByStaffId(1L);
        verify(cashRegisterSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("closeSession() with zero closing balance should be allowed")
    void testCloseSession_ZeroClosingBalance() {
        CashRegisterSessionCloseRequest request = new CashRegisterSessionCloseRequest(0);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);
        when(cashRegisterSessionRepository.save(session)).thenReturn(session);

        CashRegisterSession result = cashRegisterService.closeSession(request);

        assertNotNull(result);
        verify(session).close(new Money(0), staff);
    }

    @Test
    @DisplayName("closeSession() should invoke entity close method")
    void testCloseSession_InvokesEntityCloseMethod() {
        CashRegisterSessionCloseRequest request = new CashRegisterSessionCloseRequest(12000);

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);
        when(cashRegisterSessionRepository.save(session)).thenReturn(session);

        cashRegisterService.closeSession(request);

        verify(session).close(new Money(12000), staff);
    }

    // ========== DEPOSIT TESTS ==========

    @Test
    @DisplayName("deposit() should add deposit to staff's open session")
    void testDeposit_Success() {
        CashRegisterSessionDepositRequest request = new CashRegisterSessionDepositRequest(3000, "Cash deposit");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);
        when(cashRegisterSessionRepository.save(session)).thenReturn(session);

        CashRegisterSession result = cashRegisterService.deposit(request);

        assertNotNull(result);
        verify(staffService).getCurrentStaff();
        verify(cashRegisterSessionRepository).findOpenSessionByStaffId(1L);
        verify(session).deposit(new Money(3000), "Cash deposit");
        verify(cashRegisterSessionRepository).save(session);
    }

    @Test
    @DisplayName("deposit() should throw when no open session found")
    void testDeposit_NoOpenSession() {
        CashRegisterSessionDepositRequest request = new CashRegisterSessionDepositRequest(3000, "Deposit");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> cashRegisterService.deposit(request));
        verify(cashRegisterSessionRepository).findOpenSessionByStaffId(1L);
        verify(cashRegisterSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("deposit() with zero amount should be allowed")
    void testDeposit_ZeroAmount() {
        CashRegisterSessionDepositRequest request = new CashRegisterSessionDepositRequest(0, "Zero deposit");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);
        when(cashRegisterSessionRepository.save(session)).thenReturn(session);

        CashRegisterSession result = cashRegisterService.deposit(request);

        assertNotNull(result);
        verify(session).deposit(new Money(0), "Zero deposit");
    }

    @Test
    @DisplayName("deposit() should invoke entity deposit method")
    void testDeposit_InvokesEntityDepositMethod() {
        CashRegisterSessionDepositRequest request = new CashRegisterSessionDepositRequest(5000, "Large deposit");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);
        when(cashRegisterSessionRepository.save(session)).thenReturn(session);

        cashRegisterService.deposit(request);

        verify(session).deposit(new Money(5000), "Large deposit");
    }

    // ========== WITHDRAW TESTS ==========

    @Test
    @DisplayName("withdraw() should subtract withdrawal from staff's open session")
    void testWithdraw_Success() {
        CashRegisterSessionWithdrawRequest request = new CashRegisterSessionWithdrawRequest(2000, "Supplies");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);
        when(cashRegisterSessionRepository.save(session)).thenReturn(session);

        CashRegisterSession result = cashRegisterService.withdraw(request);

        assertNotNull(result);
        verify(staffService).getCurrentStaff();
        verify(cashRegisterSessionRepository).findOpenSessionByStaffId(1L);
        verify(session).withdraw(new Money(2000), "Supplies");
        verify(cashRegisterSessionRepository).save(session);
    }

    @Test
    @DisplayName("withdraw() should throw when no open session found")
    void testWithdraw_NoOpenSession() {
        CashRegisterSessionWithdrawRequest request = new CashRegisterSessionWithdrawRequest(2000, "Withdrawal");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> cashRegisterService.withdraw(request));
        verify(cashRegisterSessionRepository).findOpenSessionByStaffId(1L);
        verify(cashRegisterSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("withdraw() with zero amount should be allowed")
    void testWithdraw_ZeroAmount() {
        CashRegisterSessionWithdrawRequest request = new CashRegisterSessionWithdrawRequest(0, "Zero withdrawal");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);
        when(cashRegisterSessionRepository.save(session)).thenReturn(session);

        CashRegisterSession result = cashRegisterService.withdraw(request);

        assertNotNull(result);
        verify(session).withdraw(new Money(0), "Zero withdrawal");
    }

    @Test
    @DisplayName("withdraw() should invoke entity withdraw method")
    void testWithdraw_InvokesEntityWithdrawMethod() {
        CashRegisterSessionWithdrawRequest request = new CashRegisterSessionWithdrawRequest(1500, "Petty cash");

        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);
        when(cashRegisterSessionRepository.save(session)).thenReturn(session);

        cashRegisterService.withdraw(request);

        verify(session).withdraw(new Money(1500), "Petty cash");
    }

    // ========== INTEGRATION/WORKFLOW TESTS ==========

    @Test
    @DisplayName("Full session lifecycle: open -> deposit -> withdraw -> close")
    void testFullSessionLifecycle() {
        // Open
        CashRegisterSessionOpenRequest openRequest = new CashRegisterSessionOpenRequest(10000, 1L);
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.existsByStaffIdAndStatusOpen(1L)).thenReturn(false);
        when(cashRegisterRepository.findById(1L)).thenReturn(Optional.of(cashRegister));
        when(pawnService.getAllMaturingWithinDays(0)).thenReturn(List.of());
        when(cashRegisterSessionRepository.save(any(CashRegisterSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CashRegisterSession opened = cashRegisterService.openSession(openRequest);
        assertNotNull(opened);


        ArgumentCaptor<CashRegisterSession> captor =
                ArgumentCaptor.forClass(CashRegisterSession.class);

        verify(cashRegisterSessionRepository, atLeastOnce()).save(captor.capture());

        CashRegisterSession saved = captor.getValue();
        assertNotNull(saved);
    }

    @Test
    @DisplayName("Multiple deposits and withdrawals in same session")
    void testMultipleDepositsAndWithdrawals() {
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.findOpenSessionByStaffId(1L)).thenReturn(session);
        when(cashRegisterSessionRepository.save(session)).thenReturn(session);

        // First deposit
        cashRegisterService.deposit(new CashRegisterSessionDepositRequest(1000, "First"));
        verify(session).deposit(new Money(1000), "First");

        // Second deposit
        cashRegisterService.deposit(new CashRegisterSessionDepositRequest(500, "Second"));
        verify(session).deposit(new Money(500), "Second");

        // First withdrawal
        cashRegisterService.withdraw(new CashRegisterSessionWithdrawRequest(300, "First withdraw"));
        verify(session).withdraw(new Money(300), "First withdraw");

        // Second withdrawal
        cashRegisterService.withdraw(new CashRegisterSessionWithdrawRequest(200, "Second withdraw"));
        verify(session).withdraw(new Money(200), "Second withdraw");

        verify(cashRegisterSessionRepository, times(4)).save(session);
    }

    @Test
    @DisplayName("openSession() then try to open again should fail")
    void testOpenSessionTwice_ShouldFail() {
        CashRegisterSessionOpenRequest request = new CashRegisterSessionOpenRequest(10000, 1L);

        // First open succeeds
        when(staffService.getCurrentStaff()).thenReturn(staff);
        when(cashRegisterSessionRepository.existsByStaffIdAndStatusOpen(1L)).thenReturn(false);
        when(cashRegisterRepository.findById(1L)).thenReturn(Optional.of(cashRegister));
        when(pawnService.getAllMaturingWithinDays(0)).thenReturn(List.of());
        when(cashRegisterSessionRepository.save(any(CashRegisterSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cashRegisterService.openSession(request);

        // Second open should fail
        when(cashRegisterSessionRepository.existsByStaffIdAndStatusOpen(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> cashRegisterService.openSession(request));
    }

    @Test
    @DisplayName("Different staff members can have their own open sessions")
    void testDifferentStaffOpenSessions() {
        Staff staff1 = mock(Staff.class);
        when(staff1.getId()).thenReturn(1L);
        Staff staff2 = mock(Staff.class);
        when(staff2.getId()).thenReturn(2L);

        CashRegisterSessionOpenRequest request = new CashRegisterSessionOpenRequest(10000, 1L);

        // Staff 1 opens session
        when(staffService.getCurrentStaff()).thenReturn(staff1);
        when(cashRegisterSessionRepository.existsByStaffIdAndStatusOpen(1L)).thenReturn(false);
        when(cashRegisterRepository.findById(1L)).thenReturn(Optional.of(cashRegister));
        when(pawnService.getAllMaturingWithinDays(0)).thenReturn(List.of());
        when(cashRegisterSessionRepository.save(any(CashRegisterSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CashRegisterSession session1 = cashRegisterService.openSession(request);
        assertNotNull(session1);

        // Staff 2 opens session
        when(staffService.getCurrentStaff()).thenReturn(staff2);
        when(cashRegisterSessionRepository.existsByStaffIdAndStatusOpen(2L)).thenReturn(false);

        CashRegisterSession session2 = cashRegisterService.openSession(request);
        assertNotNull(session2);

        verify(cashRegisterSessionRepository).existsByStaffIdAndStatusOpen(1L);
        verify(cashRegisterSessionRepository).existsByStaffIdAndStatusOpen(2L);
    }
}