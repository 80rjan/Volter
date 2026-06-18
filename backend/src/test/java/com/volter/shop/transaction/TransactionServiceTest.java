package com.volter.shop.transaction;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.application.dto.TransactionFilterRequest;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.modules.transaction.domain.repository.TransactionRepository;
import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.shared.valueobject.Money;
import com.volter.shared.web.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private StaffService staffService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("record persists a transaction built from the given arguments")
    void record_persistsTransaction() {
        CashRegisterSession session = mock(CashRegisterSession.class);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = transactionService.record(
                42L, session, TransactionType.PAWN, new Money(1000), TransactionDirection.OUT, "Pawn principal");

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        Transaction saved = captor.getValue();

        assertEquals(42L, saved.getStaffId());
        assertSame(session, saved.getCashRegisterSession());
        assertEquals(TransactionType.PAWN, saved.getType());
        assertEquals(new Money(1000), saved.getAmount());
        assertEquals(TransactionDirection.OUT, saved.getDirection());
        assertEquals("Pawn principal", saved.getDescription());
        assertSame(saved, result);
    }

    @Test
    @DisplayName("get returns the caller's own transaction")
    void get_own() {
        Transaction tx = mock(Transaction.class);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(tx.getStaffId()).thenReturn(3L);

        assertSame(tx, transactionService.get(1L, 3L));
        verifyNoInteractions(staffService);
    }

    @Test
    @DisplayName("get returns a subordinate's transaction for their manager")
    void get_subordinate() {
        Transaction tx = mock(Transaction.class);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(tx.getStaffId()).thenReturn(5L);
        when(staffService.isManagerOf(3L, 5L)).thenReturn(true);

        assertSame(tx, transactionService.get(1L, 3L));
    }

    @Test
    @DisplayName("get forbids a transaction made by an unrelated staff member")
    void get_unrelated_throws() {
        Transaction tx = mock(Transaction.class);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(tx.getStaffId()).thenReturn(5L);
        when(staffService.isManagerOf(3L, 5L)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> transactionService.get(1L, 3L));
    }

    @Test
    @DisplayName("get throws when the transaction is missing")
    void get_missing_throws() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.get(1L, 3L));
    }

    @Test
    @DisplayName("list scopes to the caller's team and forwards to the repository")
    void list_scopesToTeam() {
        Pageable pageable = PageRequest.of(0, 20);
        when(staffService.findSubordinateStaffIds(3L)).thenReturn(java.util.List.of(4L, 5L));
        when(transactionRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(org.springframework.data.domain.Page.empty(pageable));

        transactionService.list(new TransactionFilterRequest(null, null, null, null), pageable, 3L);

        verify(staffService).findSubordinateStaffIds(3L);
        verify(transactionRepository).findAll(any(Specification.class), eq(pageable));
    }
}
