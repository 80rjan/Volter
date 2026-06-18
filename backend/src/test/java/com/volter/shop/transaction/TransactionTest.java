package com.volter.shop.transaction;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TransactionTest {

    private Transaction tx(TransactionDirection direction, int amount) {
        return Transaction.create(1L, mock(CashRegisterSession.class), TransactionType.PAWN,
                new Money(amount), direction, "test");
    }

    @Test
    @DisplayName("an IN transaction is an inflow with a positive signed amount")
    void inflow() {
        Transaction t = tx(TransactionDirection.IN, 500);

        assertTrue(t.isInflow());
        assertFalse(t.isOutflow());
        assertEquals(500, t.signedAmount());
    }

    @Test
    @DisplayName("an OUT transaction is an outflow with a negative signed amount")
    void outflow() {
        Transaction t = tx(TransactionDirection.OUT, 500);

        assertTrue(t.isOutflow());
        assertFalse(t.isInflow());
        assertEquals(-500, t.signedAmount());
    }
}
