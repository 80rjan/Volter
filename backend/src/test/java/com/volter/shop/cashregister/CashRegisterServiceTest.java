package com.volter.shop.cashregister;

import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.application.dto.CashRegisterCreateRequest;
import com.volter.shop.modules.cashregister.application.dto.CashRegisterTransactionRecordRequest;
import com.volter.shop.modules.cashregister.application.dto.DiscrepancyFilterRequest;
import com.volter.shop.modules.cashregister.application.dto.DiscrepancyResolveRequest;
import com.volter.shop.modules.cashregister.application.dto.SessionCloseRequest;
import com.volter.shop.modules.cashregister.application.dto.SessionFilterRequest;
import com.volter.shop.modules.cashregister.application.dto.SessionOpenRequest;
import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSessionDiscrepancy;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterTransaction;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionStatus;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterTransactionAction;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterRepository;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterSessionDiscrepancyRepository;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterSessionRepository;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterTransactionRepository;
import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.pawn.application.PawnDueQueryService;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.shared.valueobject.Money;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashRegisterServiceTest {

    @Mock private CashRegisterRepository cashRegisterRepository;
    @Mock private CashRegisterSessionRepository sessionRepository;
    @Mock private CashRegisterTransactionRepository cashTxRepository;
    @Mock private CashRegisterSessionDiscrepancyRepository discrepancyRepository;
    @Mock private TransactionService transactionService;
    @Mock private PawnDueQueryService pawnDueQueryService;
    @Mock private StaffService staffService;

    @InjectMocks
    private CashRegisterService service;

    @Nested
    @DisplayName("createRegister()")
    class CreateRegister {

        @Test
        @DisplayName("rejects a duplicate code")
        void duplicateCode_throws() {
            when(cashRegisterRepository.existsByCode("R1")).thenReturn(true);

            assertThrows(BusinessRuleException.class,
                    () -> service.createRegister(new CashRegisterCreateRequest("R1")));
            verify(cashRegisterRepository, never()).save(any());
        }

        @Test
        @DisplayName("saves a register with the given code")
        void savesRegister() {
            when(cashRegisterRepository.existsByCode("R1")).thenReturn(false);
            when(cashRegisterRepository.save(any(CashRegister.class))).thenAnswer(inv -> inv.getArgument(0));

            CashRegister created = service.createRegister(new CashRegisterCreateRequest("R1"));

            assertEquals("R1", created.getCode());
        }
    }

    @Nested
    @DisplayName("openSession()")
    class OpenSession {

        @Test
        @DisplayName("rejects opening a second session while one is already OPEN")
        void alreadyOpen_throws() {
            when(cashRegisterRepository.findById(1L)).thenReturn(Optional.of(mock(CashRegister.class)));
            when(sessionRepository.findByCashRegister_IdAndStatus(1L, CashRegisterSessionStatus.OPEN))
                    .thenReturn(Optional.of(mock(CashRegisterSession.class)));

            assertThrows(BusinessRuleException.class,
                    () -> service.openSession(1L, new SessionOpenRequest(500), 3L));
            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("opens and saves a new session seeded with the opening balance")
        void opensSession() {
            when(cashRegisterRepository.findById(1L)).thenReturn(Optional.of(mock(CashRegister.class)));
            when(sessionRepository.findByCashRegister_IdAndStatus(1L, CashRegisterSessionStatus.OPEN))
                    .thenReturn(Optional.empty());
            when(pawnDueQueryService.totalInterestDue(0)).thenReturn(new Money(0));
            when(sessionRepository.save(any(CashRegisterSession.class))).thenAnswer(inv -> inv.getArgument(0));

            CashRegisterSession session = service.openSession(1L, new SessionOpenRequest(500), 3L);

            assertEquals(500, session.getOpeningBalance());
            assertEquals(500, session.getCurrentBalance());
            assertEquals(3L, session.getStaffId());
        }
    }

    @Nested
    @DisplayName("closeSession()")
    class CloseSession {

        @Test
        @DisplayName("persists a discrepancy when closing reveals one")
        void persistsDiscrepancy() {
            CashRegisterSession session = mock(CashRegisterSession.class);
            CashRegisterSessionDiscrepancy discrepancy = mock(CashRegisterSessionDiscrepancy.class);
            when(sessionRepository.findByCashRegister_IdAndStatus(1L, CashRegisterSessionStatus.OPEN))
                    .thenReturn(Optional.of(session));
            when(session.getStaffId()).thenReturn(3L);   // caller is the operator
            when(session.close(480)).thenReturn(discrepancy);

            service.closeSession(1L, new SessionCloseRequest(480), 3L);

            verify(discrepancyRepository).save(discrepancy);
        }

        @Test
        @DisplayName("saves no discrepancy when the count matches")
        void noDiscrepancy() {
            CashRegisterSession session = mock(CashRegisterSession.class);
            when(sessionRepository.findByCashRegister_IdAndStatus(1L, CashRegisterSessionStatus.OPEN))
                    .thenReturn(Optional.of(session));
            when(session.getStaffId()).thenReturn(3L);
            when(session.close(500)).thenReturn(null);

            service.closeSession(1L, new SessionCloseRequest(500), 3L);

            verify(discrepancyRepository, never()).save(any());
        }

        @Test
        @DisplayName("only the session's operator may close it")
        void nonOperator_throws() {
            CashRegisterSession session = mock(CashRegisterSession.class);
            when(sessionRepository.findByCashRegister_IdAndStatus(1L, CashRegisterSessionStatus.OPEN))
                    .thenReturn(Optional.of(session));
            when(session.getStaffId()).thenReturn(9L);   // operator is someone else

            assertThrows(AccessDeniedException.class,
                    () -> service.closeSession(1L, new SessionCloseRequest(500), 3L));
            verify(session, never()).close(anyInt());
        }
    }

    @Nested
    @DisplayName("recordTransaction()")
    class RecordTransaction {

        private final CashRegisterTransactionRecordRequest request = new CashRegisterTransactionRecordRequest(
                CashRegisterTransactionAction.DEPOSIT, TransactionDirection.IN, 200, "manual deposit");

        @Test
        @DisplayName("refuses to record against a closed session")
        void closedSession_throws() {
            CashRegisterSession session = mock(CashRegisterSession.class);
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(session.isOpen()).thenReturn(false);

            assertThrows(BusinessRuleException.class, () -> service.recordTransaction(1L, request, 3L));
            verifyNoInteractions(transactionService);
        }

        @Test
        @DisplayName("records the ledger entry, applies it and stores the cash-register transaction")
        void recordsAndStores() {
            CashRegisterSession session = mock(CashRegisterSession.class);
            Transaction tx = mock(Transaction.class);
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(session.isOpen()).thenReturn(true);
            when(transactionService.record(eq(3L), eq(session), any(), eq(new Money(200)),
                    eq(TransactionDirection.IN), eq("manual deposit"))).thenReturn(tx);
            // applyTransaction (self-invoked) reads the session off the transaction
            when(tx.getCashRegisterSession()).thenReturn(session);
            when(tx.isInflow()).thenReturn(true);
            when(tx.getAmount()).thenReturn(new Money(200));

            service.recordTransaction(1L, request, 3L);

            verify(session).deposit(new Money(200));
            verify(cashTxRepository).save(any(CashRegisterTransaction.class));
        }
    }

    @Nested
    @DisplayName("applyTransaction()")
    class ApplyTransaction {

        @Test
        @DisplayName("an inflow deposits into the session balance")
        void inflow_deposits() {
            CashRegisterSession session = mock(CashRegisterSession.class);
            Transaction tx = mock(Transaction.class);
            when(tx.getCashRegisterSession()).thenReturn(session);
            when(tx.isInflow()).thenReturn(true);
            when(tx.getAmount()).thenReturn(new Money(300));

            service.applyTransaction(tx);

            verify(session).deposit(new Money(300));
            verify(session, never()).withdraw(any());
        }

        @Test
        @DisplayName("an outflow withdraws from the session balance")
        void outflow_withdraws() {
            CashRegisterSession session = mock(CashRegisterSession.class);
            Transaction tx = mock(Transaction.class);
            when(tx.getCashRegisterSession()).thenReturn(session);
            when(tx.isInflow()).thenReturn(false);
            when(tx.getAmount()).thenReturn(new Money(300));

            service.applyTransaction(tx);

            verify(session).withdraw(new Money(300));
            verify(session, never()).deposit(any());
        }
    }

    @Nested
    @DisplayName("resolveDiscrepancy() / requireOpenSession()")
    class Misc {

        @Test
        @DisplayName("cannot resolve an already-resolved discrepancy")
        void alreadyResolved_throws() {
            CashRegisterSessionDiscrepancy discrepancy = mock(CashRegisterSessionDiscrepancy.class);
            when(discrepancyRepository.findById(1L)).thenReturn(Optional.of(discrepancy));
            when(discrepancy.isResolved()).thenReturn(true);

            assertThrows(BusinessRuleException.class,
                    () -> service.resolveDiscrepancy(1L, new DiscrepancyResolveRequest("note"), 3L));
        }

        @Test
        @DisplayName("a manager resolves a discrepancy of one of their subordinates")
        void resolves() {
            CashRegisterSessionDiscrepancy discrepancy = mock(CashRegisterSessionDiscrepancy.class);
            CashRegisterSession session = mock(CashRegisterSession.class);
            when(discrepancyRepository.findById(1L)).thenReturn(Optional.of(discrepancy));
            when(discrepancy.isResolved()).thenReturn(false);
            when(discrepancy.getSession()).thenReturn(session);
            when(session.getStaffId()).thenReturn(5L);          // operator who caused it
            when(staffService.isManagerOf(3L, 5L)).thenReturn(true);

            service.resolveDiscrepancy(1L, new DiscrepancyResolveRequest("counted twice"), 3L);

            verify(discrepancy).resolve(3L, "counted twice");
        }

        @Test
        @DisplayName("a non-manager of the operator is forbidden from resolving the discrepancy")
        void notManager_throws() {
            CashRegisterSessionDiscrepancy discrepancy = mock(CashRegisterSessionDiscrepancy.class);
            CashRegisterSession session = mock(CashRegisterSession.class);
            when(discrepancyRepository.findById(1L)).thenReturn(Optional.of(discrepancy));
            when(discrepancy.isResolved()).thenReturn(false);
            when(discrepancy.getSession()).thenReturn(session);
            when(session.getStaffId()).thenReturn(5L);
            when(staffService.isManagerOf(3L, 5L)).thenReturn(false);

            assertThrows(AccessDeniedException.class,
                    () -> service.resolveDiscrepancy(1L, new DiscrepancyResolveRequest("x"), 3L));
            verify(discrepancy, never()).resolve(any(), any());
        }

        @Test
        @DisplayName("requireOpenSession rejects a closed session")
        void requireOpenSession_closed_throws() {
            CashRegisterSession session = mock(CashRegisterSession.class);
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(session.isOpen()).thenReturn(false);

            assertThrows(BusinessRuleException.class, () -> service.requireOpenSession(1L));
        }

        @Test
        @DisplayName("getSession throws when the session is missing")
        void getSession_missing_throws() {
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.getSession(1L, 3L));
        }

        @Test
        @DisplayName("getSession returns the caller's own session")
        void getSession_own_ok() {
            CashRegisterSession session = mock(CashRegisterSession.class);
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(session.getStaffId()).thenReturn(3L);   // caller is the operator

            assertSame(session, service.getSession(1L, 3L));
            verifyNoInteractions(staffService);
        }

        @Test
        @DisplayName("getSession lets a manager view a subordinate's session")
        void getSession_managerOfOperator_ok() {
            CashRegisterSession session = mock(CashRegisterSession.class);
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(session.getStaffId()).thenReturn(5L);   // operated by someone else
            when(staffService.isManagerOf(3L, 5L)).thenReturn(true);

            assertSame(session, service.getSession(1L, 3L));
        }

        @Test
        @DisplayName("getSession forbids viewing an unrelated staff member's session")
        void getSession_unrelated_throws() {
            CashRegisterSession session = mock(CashRegisterSession.class);
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(session.getStaffId()).thenReturn(5L);
            when(staffService.isManagerOf(3L, 5L)).thenReturn(false);

            assertThrows(AccessDeniedException.class, () -> service.getSession(1L, 3L));
        }
    }

    @Nested
    @DisplayName("scoped listing (manager's team)")
    class ScopedListing {

        private final Pageable pageable = PageRequest.of(0, 10);
        private final SessionFilterRequest sessionFilter = new SessionFilterRequest(null, null, null, null, null);
        private final DiscrepancyFilterRequest discrepancyFilter = new DiscrepancyFilterRequest(null, null, null, null);

        @Test
        @DisplayName("sessions resolve the manager's team and query within it")
        void sessions_usesTeam() {
            when(staffService.findSubordinateStaffIds(7L)).thenReturn(new java.util.ArrayList<>(List.of(2L, 3L)));
            when(sessionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));

            service.listSessions(7L, sessionFilter, pageable);

            verify(staffService).findSubordinateStaffIds(7L);
            verify(sessionRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("discrepancies resolve the manager's team and query within it")
        void discrepancies_usesTeam() {
            when(staffService.findSubordinateStaffIds(7L)).thenReturn(new java.util.ArrayList<>(List.of(2L, 3L)));
            when(discrepancyRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));

            service.listDiscrepancies(7L, discrepancyFilter, pageable);

            verify(staffService).findSubordinateStaffIds(7L);
            verify(discrepancyRepository).findAll(any(Specification.class), eq(pageable));
        }
    }
}
