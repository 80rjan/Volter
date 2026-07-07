package skit_project.tests.syntax;

import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Using PIT (https://pitest.org/) to test the mutation coverage of PawnContract's redemption arithmetic.
 * Run with command: ./mvnw -Ppit test-compile org.pitest:pitest-maven:mutationCoverage
 * Results in: target/pit-reports/index.html
 */
@DisplayName("Syntax-based testing (mutation) — PawnContract redemption arithmetic")
class ExpectedRedemptionMutationTest {

    private PawnContract contract(LocalDate issueDate, int principal, int interest, int termDays) {
        // customer/item are irrelevant to the redemption arithmetic and are never
        // dereferenced by it, so we pass null and keep this test free of Mockito.
        return PawnContract.create(
                null, null, 9L,
                new Money(principal), new Money(interest), termDays, issueDate);
    }

    /** principal (1000) != interest (100) so that operand/operator mutants become observable. */
    private PawnContract activeToday() {
        return contract(LocalDate.now(), 1000, 100, 30);        // due in 30 days -> not matured
    }

    private PawnContract maturedContract() {
        return contract(LocalDate.now().minusDays(40), 1000, 100, 30);   // due 10 days ago -> matured
    }

    @Test
    @DisplayName("totalDue = principal + interest  (kills PIT NullReturnVals on totalDue L191)")
    void totalDue_isPrincipalPlusInterest() {
        assertEquals(new Money(1100), activeToday().totalDue());
        // PIT's NullReturnVals mutant makes totalDue() return null -> assertEquals fails (killed).
    }

    @Test
    @DisplayName("isMatured false within term, true past due (kills PIT BooleanTrue/BooleanFalse returns on isMatured L209)")
    void isMatured_reflectsDueDate() {
        assertFalse(activeToday().isMatured());     // a 'return true' mutant (always matured) fails here
        assertTrue(maturedContract().isMatured());  // a 'return false' mutant (never matured) fails here
    }

    @Test
    @DisplayName("Not matured: expected == totalDue, no penalty (kills PIT NegateConditionals + NullReturnVals on expectedRedemptionAmount L201)")
    void notMatured_expectedHasNoPenalty() {
        assertEquals(new Money(1100), activeToday().expectedRedemptionAmount());
        // Negating the ternary condition adds the penalty (returns 1200); a null return also fails here.
    }

    @Test
    @DisplayName("Matured: expected == totalDue + one interest penalty (kills the same expectedRedemptionAmount L201 mutants on the matured branch)")
    void matured_expectedAddsInterestPenalty() {
        assertEquals(new Money(1200), maturedContract().expectedRedemptionAmount());
        // Negating the ternary drops the penalty (returns 1100); a null return also fails here.
    }
}
