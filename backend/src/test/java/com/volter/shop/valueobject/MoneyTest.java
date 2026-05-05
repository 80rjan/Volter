package com.volter.shop.valueobject;

import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    // =========================================================================
    // Construction
    // =========================================================================

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("valid amount is accepted")
        void validAmount_accepted() {
            Money money = new Money(100);
            assertEquals(100, money.amount());
        }

        @Test
        @DisplayName("zero amount is accepted")
        void zeroAmount_accepted() {
            Money money = new Money(0);
            assertEquals(0, money.amount());
        }

        @Test
        @DisplayName("null amount throws IllegalArgumentException")
        void nullAmount_throws() {
            assertThrows(IllegalArgumentException.class, () -> new Money(null));
        }

        @Test
        @DisplayName("negative amount throws IllegalArgumentException")
        void negativeAmount_throws() {
            assertThrows(IllegalArgumentException.class, () -> new Money(-1));
        }

        @Test
        @DisplayName("two Money instances with same amount are equal")
        void sameAmount_areEqual() {
            assertEquals(new Money(500), new Money(500));
        }

        @Test
        @DisplayName("two Money instances with different amounts are not equal")
        void differentAmount_areNotEqual() {
            assertNotEquals(new Money(100), new Money(200));
        }
    }

    // =========================================================================
    // add()
    // =========================================================================

    @Nested
    @DisplayName("add()")
    class Add {

        @Test
        @DisplayName("adds two positive amounts")
        void addsTwoPositiveAmounts() {
            assertEquals(new Money(300), new Money(100).add(new Money(200)));
        }

        @Test
        @DisplayName("adding zero returns same amount")
        void addingZero_returnsSameAmount() {
            assertEquals(new Money(500), new Money(500).add(new Money(0)));
        }

        @Test
        @DisplayName("null argument throws IllegalArgumentException")
        void nullArgument_throws() {
            assertThrows(IllegalArgumentException.class, () -> new Money(100).add(null));
        }
    }

    // =========================================================================
    // subtract()
    // =========================================================================

    @Nested
    @DisplayName("subtract()")
    class Subtract {

        @Test
        @DisplayName("subtracts smaller from larger")
        void subtractsSmallerFromLarger() {
            assertEquals(new Money(300), new Money(500).subtract(new Money(200)));
        }

        @Test
        @DisplayName("subtracting equal amount yields zero")
        void subtractingEqualAmount_yieldsZero() {
            assertEquals(new Money(0), new Money(500).subtract(new Money(500)));
        }

        @Test
        @DisplayName("subtracting larger from smaller throws IllegalArgumentException")
        void subtractingLargerFromSmaller_throws() {
            assertThrows(IllegalArgumentException.class, () -> new Money(100).subtract(new Money(200)));
        }

        @Test
        @DisplayName("null argument throws IllegalArgumentException")
        void nullArgument_throws() {
            assertThrows(IllegalArgumentException.class, () -> new Money(100).subtract(null));
        }
    }

    // =========================================================================
    // absoluteSubtract()
    // =========================================================================

    @Nested
    @DisplayName("absoluteSubtract()")
    class AbsoluteSubtract {

        @ParameterizedTest
        @CsvSource({
                "1000, 800, 200",   // positive difference
                "800, 1000, 200",   // negative difference → absolute
                "500, 500, 0"       // equal → zero
        })
        @DisplayName("returns absolute difference")
        void returnsAbsoluteDifference(int a, int b, int expected) {
            assertEquals(new Money(expected), new Money(a).absoluteSubtract(new Money(b)));
        }

        @Test
        @DisplayName("null argument throws IllegalArgumentException")
        void nullArgument_throws() {
            assertThrows(IllegalArgumentException.class, () -> new Money(100).absoluteSubtract(null));
        }
    }

    // =========================================================================
    // multiply()
    // =========================================================================

    @Nested
    @DisplayName("multiply()")
    class Multiply {

        @Test
        @DisplayName("multiplies by a positive factor")
        void multipliesByPositiveFactor() {
            assertEquals(new Money(300), new Money(100).multiply(new BigDecimal("3")));
        }

        @Test
        @DisplayName("multiplying by zero yields zero")
        void multiplyingByZero_yieldsZero() {
            assertEquals(new Money(0), new Money(500).multiply(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("multiplying by one returns same amount")
        void multiplyingByOne_returnsSameAmount() {
            assertEquals(new Money(500), new Money(500).multiply(BigDecimal.ONE));
        }

        @Test
        @DisplayName("result is rounded half-up")
        void result_isRoundedHalfUp() {
            // 100 * 1.5 = 150
            assertEquals(new Money(150), new Money(100).multiply(new BigDecimal("1.5")));
        }

        @Test
        @DisplayName("null factor throws IllegalArgumentException")
        void nullFactor_throws() {
            assertThrows(IllegalArgumentException.class, () -> new Money(100).multiply(null));
        }

        @Test
        @DisplayName("negative factor throws IllegalArgumentException")
        void negativeFactor_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Money(100).multiply(new BigDecimal("-1")));
        }
    }

    // =========================================================================
    // divide()
    // =========================================================================

    @Nested
    @DisplayName("divide()")
    class Divide {

        @Test
        @DisplayName("divides by a positive divisor")
        void dividesByPositiveDivisor() {
            assertEquals(new Money(50), new Money(100).divide(new BigDecimal("2")));
        }

        @Test
        @DisplayName("dividing by one returns same amount")
        void dividingByOne_returnsSameAmount() {
            assertEquals(new Money(500), new Money(500).divide(BigDecimal.ONE));
        }

        @Test
        @DisplayName("result is rounded half-up")
        void result_isRoundedHalfUp() {
            // 100 / 3 = 33.33... → rounds to 33
            assertEquals(new Money(33), new Money(100).divide(new BigDecimal("3")));
        }

        @Test
        @DisplayName("dividing by zero throws IllegalArgumentException")
        void dividingByZero_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Money(100).divide(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("null divisor throws IllegalArgumentException")
        void nullDivisor_throws() {
            assertThrows(IllegalArgumentException.class, () -> new Money(100).divide(null));
        }

        @Test
        @DisplayName("negative divisor throws IllegalArgumentException")
        void negativeDivisor_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Money(100).divide(new BigDecimal("-2")));
        }
    }

    // =========================================================================
    // isLessThan() / isGreaterThan() / isZero()
    // =========================================================================

    @Nested
    @DisplayName("Comparison methods")
    class Comparisons {

        @Test
        @DisplayName("isLessThan returns true when this < other")
        void isLessThan_trueWhenSmaller() {
            assertTrue(new Money(100).isLessThan(new Money(200)));
        }

        @Test
        @DisplayName("isLessThan returns false when this == other")
        void isLessThan_falseWhenEqual() {
            assertFalse(new Money(100).isLessThan(new Money(100)));
        }

        @Test
        @DisplayName("isLessThan returns false when this > other")
        void isLessThan_falseWhenGreater() {
            assertFalse(new Money(200).isLessThan(new Money(100)));
        }

        @Test
        @DisplayName("isGreaterThan returns true when this > other")
        void isGreaterThan_trueWhenLarger() {
            assertTrue(new Money(200).isGreaterThan(new Money(100)));
        }

        @Test
        @DisplayName("isGreaterThan returns false when this == other")
        void isGreaterThan_falseWhenEqual() {
            assertFalse(new Money(100).isGreaterThan(new Money(100)));
        }

        @Test
        @DisplayName("isGreaterThan returns false when this < other")
        void isGreaterThan_falseWhenSmaller() {
            assertFalse(new Money(100).isGreaterThan(new Money(200)));
        }

        @Test
        @DisplayName("isZero returns true for zero amount")
        void isZero_trueForZero() {
            assertTrue(new Money(0).isZero());
        }

        @Test
        @DisplayName("isZero returns false for non-zero amount")
        void isZero_falseForNonZero() {
            assertFalse(new Money(1).isZero());
        }
    }
}
