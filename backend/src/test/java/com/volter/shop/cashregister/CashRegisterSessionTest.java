package com.volter.shop.cashregister;

import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSessionDiscrepancy;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyType;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionStatus;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CashRegisterSessionTest {

    private CashRegisterSession openSession(int opening) {
        return CashRegisterSession.open(CashRegister.create("R1"), 7L, opening, new Money(0));
    }

    @Nested
    @DisplayName("open()")
    class Open {

        @Test
        @DisplayName("seeds the running balance with the opening balance")
        void seedsCurrentBalance() {
            CashRegisterSession session = openSession(1000);

            assertEquals(1000, session.getOpeningBalance());
            assertEquals(1000, session.getCurrentBalance());
        }

        @Test
        @DisplayName("starts OPEN")
        void startsOpen() {
            assertEquals(CashRegisterSessionStatus.OPEN, openSession(0).getStatus());
            assertTrue(openSession(0).isOpen());
        }

        @Test
        @DisplayName("defaults expected interest to zero when null is passed")
        void nullExpectedInterest_defaultsToZero() {
            CashRegisterSession session = CashRegisterSession.open(CashRegister.create("R1"), 7L, 100, null);

            assertEquals(new Money(0), session.getExpectedInterest());
        }
    }

    @Nested
    @DisplayName("deposit() / withdraw()")
    class Movements {

        @Test
        @DisplayName("deposit increases the running balance")
        void depositIncreases() {
            CashRegisterSession session = openSession(1000);

            session.deposit(new Money(250));

            assertEquals(1250, session.getCurrentBalance());
        }

        @Test
        @DisplayName("withdraw decreases the running balance")
        void withdrawDecreases() {
            CashRegisterSession session = openSession(1000);

            session.withdraw(new Money(400));

            assertEquals(600, session.getCurrentBalance());
        }

        @Test
        @DisplayName("the running balance may legitimately go negative")
        void balanceMayGoNegative() {
            CashRegisterSession session = openSession(100);

            session.withdraw(new Money(300));

            assertEquals(-200, session.getCurrentBalance());
        }

        @Test
        @DisplayName("does not allow movements on a CLOSED session")
        void closedSession_rejectsMovements() {
            CashRegisterSession session = openSession(100);
            session.close(100);

            assertThrows(IllegalStateException.class, () -> session.deposit(new Money(10)));
            assertThrows(IllegalStateException.class, () -> session.withdraw(new Money(10)));
        }
    }

    @Nested
    @DisplayName("close()")
    class Close {

        @Test
        @DisplayName("records closing state and marks the session CLOSED")
        void recordsClosingState() {
            CashRegisterSession session = openSession(500);

            session.close(500);

            assertEquals(CashRegisterSessionStatus.CLOSED, session.getStatus());
            assertEquals(500, session.getClosingBalance());
            assertNotNull(session.getClosedAt());
            assertFalse(session.isOpen());
        }

        @Test
        @DisplayName("returns no discrepancy when the counted balance matches the running balance")
        void countedMatchesRunning_noDiscrepancy() {
            CashRegisterSession session = openSession(500);
            session.deposit(new Money(100)); // running balance now 600

            CashRegisterSessionDiscrepancy discrepancy = session.close(600);

            assertNull(discrepancy);
        }

        @Test
        @DisplayName("produces an OVERAGE when more cash is counted than expected")
        void overCount_producesOverage() {
            CashRegisterSession session = openSession(500);

            CashRegisterSessionDiscrepancy discrepancy = session.close(540);

            assertNotNull(discrepancy);
            assertEquals(CashRegisterSessionDiscrepancyType.OVERAGE, discrepancy.getType());
            assertEquals(500, discrepancy.getExpectedAmount());
            assertEquals(540, discrepancy.getCountedAmount());
            assertEquals(40, discrepancy.getDifference());
        }

        @Test
        @DisplayName("produces a SHORTAGE when less cash is counted than expected")
        void underCount_producesShortage() {
            CashRegisterSession session = openSession(500);

            CashRegisterSessionDiscrepancy discrepancy = session.close(460);

            assertNotNull(discrepancy);
            assertEquals(CashRegisterSessionDiscrepancyType.SHORTAGE, discrepancy.getType());
            assertEquals(-40, discrepancy.getDifference());
        }

        @Test
        @DisplayName("discrepancy is measured against the running balance, not the opening balance")
        void discrepancyMeasuredAgainstRunningBalance() {
            CashRegisterSession session = openSession(500);
            session.withdraw(new Money(200)); // running balance now 300

            CashRegisterSessionDiscrepancy discrepancy = session.close(300);

            assertNull(discrepancy);
        }

        @Test
        @DisplayName("cannot close an already CLOSED session")
        void doubleClose_throws() {
            CashRegisterSession session = openSession(100);
            session.close(100);

            assertThrows(IllegalStateException.class, () -> session.close(100));
        }
    }

    @Nested
    @DisplayName("addExpectedInterest()")
    class ExpectedInterest {

        @Test
        @DisplayName("accumulates expected interest")
        void accumulates() {
            CashRegisterSession session = openSession(0);

            session.addExpectedInterest(new Money(30));
            session.addExpectedInterest(new Money(20));

            assertEquals(new Money(50), session.getExpectedInterest());
        }
    }
}
