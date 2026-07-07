package skit_project.tests.property_based;

import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.time.LocalDate;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


@DisplayName("Property-based testing — Money & PawnContract invariants")
class PawnPropertyBasedTest {

    // static so it advances across the repetitions (JUnit uses a fresh instance per invocation);
    // fixed seed => reproducible sequence of inputs.
    private static final Random RND = new Random(20260703L);

    private static final int MAX = 1_000_000;

    private int nonNegative()  { return RND.nextInt(MAX + 1); }        // 0 .. 1_000_000
    private int positive()     { return 1 + RND.nextInt(MAX); }        // 1 .. 1_000_000
    private int term()         { return 1 + RND.nextInt(365); }        // 1 .. 365 days

    private PawnContract freshContract(int principal, int interest, int term, LocalDate issue) {
        return PawnContract.create(mock(Customer.class), mock(Item.class), 9L,
                new Money(principal), new Money(interest), term, issue);
    }

    // ===== Money properties ===================================================

    @RepeatedTest(200)
    @DisplayName("P1: add is exact and commutative for any two non-negative amounts")
    void moneyAdd_isExactAndCommutative() {
        int a = nonNegative(), b = nonNegative();

        assertEquals(a + b, new Money(a).add(new Money(b)).amount());
        assertEquals(new Money(a).add(new Money(b)), new Money(b).add(new Money(a)));
    }

    @RepeatedTest(200)
    @DisplayName("P2: subtract is the inverse of add — (a+b)-b == a")
    void moneySubtract_invertsAdd() {
        int a = nonNegative(), b = nonNegative();

        Money sum = new Money(a).add(new Money(b));
        assertEquals(new Money(a), sum.subtract(new Money(b)));
    }

    @RepeatedTest(100)
    @DisplayName("P3: a negative amount is always rejected")
    void money_rejectsNegative() {
        int negative = -1 - RND.nextInt(MAX);

        assertThrows(IllegalArgumentException.class, () -> new Money(negative));
    }

    // ===== PawnContract properties ============================================

    @RepeatedTest(200)
    @DisplayName("P4: a fresh contract's due date = issue + term (strictly after issue) and equals the original due date")
    void contract_dueDateDerivation() {
        int term = term();
        LocalDate issue = LocalDate.now();

        PawnContract c = freshContract(positive(), nonNegative(), term, issue);

        assertEquals(issue.plusDays(term), c.getDueDate());
        assertTrue(c.getDueDate().isAfter(issue));          // term >= 1
        assertEquals(c.getDueDate(), c.getOriginalDueDate());
    }

    @RepeatedTest(200)
    @DisplayName("P5: totalDue == principal + interest, and (when not matured) equals the expected redemption amount")
    void contract_totalDueAndExpected() {
        int principal = positive(), interest = nonNegative();

        PawnContract c = freshContract(principal, interest, term(), LocalDate.now());

        assertEquals(new Money(principal + interest), c.totalDue());
        assertFalse(c.isMatured());                                  // issued today -> not matured
        assertEquals(0, c.daysOverdue());
        assertEquals(c.totalDue(), c.expectedRedemptionAmount());    // no late penalty before maturity
    }

    @RepeatedTest(200)
    @DisplayName("P6: extending with any non-negative interest never moves the due date backwards and never touches the original due date")
    void contract_extendIsMonotonic() {
        int interest = positive();                 // >= 1 so interest-per-day is well defined
        int term = term();
        PawnContract c = freshContract(positive(), interest, term, LocalDate.now());

        LocalDate dueBefore = c.getDueDate();
        LocalDate originalBefore = c.getOriginalDueDate();
        int interestPaid = RND.nextInt(5 * interest + 1);   // 0 .. 5x one period's interest

        c.extend(new Money(interestPaid), new Money(0));

        assertFalse(c.getDueDate().isBefore(dueBefore));    // monotonic: newDue >= oldDue
        assertEquals(originalBefore, c.getOriginalDueDate()); // original due date is untouched
        assertEquals(1, c.getExtensions().size());
    }
}
