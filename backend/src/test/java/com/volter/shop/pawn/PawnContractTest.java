package com.volter.shop.pawn;

import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.PawnContractExtension;
import com.volter.shop.modules.pawn.domain.model.enums.PawnContractStatus;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PawnContractTest {

    private PawnContract contract(LocalDate issueDate, int principal, int interest, int termDays) {
        return PawnContract.create(
                mock(Customer.class), mock(Item.class), 9L,
                new Money(principal), new Money(interest), termDays, issueDate);
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("derives the due date from issue date plus term")
        void derivesDueDate() {
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);

            assertEquals(LocalDate.of(2025, 1, 31), c.getDueDate());
            assertEquals(LocalDate.of(2025, 1, 31), c.getOriginalDueDate());
        }

        @Test
        @DisplayName("starts ACTIVE")
        void startsActive() {
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);

            assertEquals(PawnContractStatus.ACTIVE, c.getStatus());
            assertTrue(c.isActive());
        }

        @Test
        @DisplayName("total due is principal plus interest")
        void totalDue() {
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);

            assertEquals(new Money(1120), c.totalDue());
        }
    }

    @Nested
    @DisplayName("extend()")
    class Extend {

        @Test
        @DisplayName("pushes the due date out proportionally to the interest paid")
        void pushesDueDate() {
            // interest 120 over 30 days => 4/day; paying 60 buys 15 days
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);

            c.extend(new Money(60), new Money(0));

            assertEquals(LocalDate.of(2025, 2, 15), c.getDueDate());
        }

        @Test
        @DisplayName("leaves the original due date untouched")
        void originalDueDateUnchanged() {
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);

            c.extend(new Money(60), new Money(0));

            assertEquals(LocalDate.of(2025, 1, 31), c.getOriginalDueDate());
        }

        @Test
        @DisplayName("records the extension with the previous and new due dates")
        void recordsExtension() {
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);

            PawnContractExtension ext = c.extend(new Money(60), new Money(10));

            assertEquals(LocalDate.of(2025, 1, 31), ext.getPreviousDueDate());
            assertEquals(LocalDate.of(2025, 2, 15), ext.getNewDueDate());
            assertEquals(new Money(60), ext.getInterestPaid());
            assertEquals(new Money(10), ext.getFee());
        }

        @Test
        @DisplayName("appends the extension to the contract's extension list")
        void appendsToExtensions() {
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);

            PawnContractExtension ext = c.extend(new Money(60), new Money(0));

            assertEquals(1, c.getExtensions().size());
            assertSame(ext, c.getExtensions().get(0));
        }

        @Test
        @DisplayName("cannot extend a non-active contract")
        void nonActive_throws() {
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);
            c.redeem();

            assertThrows(IllegalStateException.class, () -> c.extend(new Money(60), new Money(0)));
        }
    }

    @Nested
    @DisplayName("redeem() / forfeit()")
    class Terminal {

        @Test
        @DisplayName("redeem marks the contract REDEEMED and stamps the time")
        void redeem() {
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);

            c.redeem();

            assertEquals(PawnContractStatus.REDEEMED, c.getStatus());
            assertNotNull(c.getRedeemedAt());
            assertFalse(c.isActive());
        }

        @Test
        @DisplayName("forfeit marks the contract FORFEITED and stamps the time")
        void forfeit() {
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);

            c.forfeit();

            assertEquals(PawnContractStatus.FORFEITED, c.getStatus());
            assertNotNull(c.getForfeitedAt());
        }

        @Test
        @DisplayName("a redeemed contract cannot be forfeited")
        void redeemedThenForfeit_throws() {
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);
            c.redeem();

            assertThrows(IllegalStateException.class, c::forfeit);
        }

        @Test
        @DisplayName("a forfeited contract cannot be redeemed")
        void forfeitedThenRedeem_throws() {
            PawnContract c = contract(LocalDate.of(2025, 1, 1), 1000, 120, 30);
            c.forfeit();

            assertThrows(IllegalStateException.class, c::redeem);
        }
    }

    @Nested
    @DisplayName("maturity")
    class Maturity {

        @Test
        @DisplayName("a contract past its due date is matured and overdue")
        void pastDue_isMatured() {
            PawnContract c = contract(LocalDate.now().minusDays(40), 1000, 120, 30);

            assertTrue(c.isMatured());
            assertEquals(10, c.daysOverdue());
        }

        @Test
        @DisplayName("a contract within its term is neither matured nor overdue")
        void withinTerm_notMatured() {
            PawnContract c = contract(LocalDate.now(), 1000, 120, 30);

            assertFalse(c.isMatured());
            assertEquals(0, c.daysOverdue());
        }
    }
}
