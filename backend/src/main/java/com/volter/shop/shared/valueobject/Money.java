package com.volter.shop.shared.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
public record Money (
        Integer amount
) {
    public Money {
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
    }

    public Money subtract(Money other) {
        if (other == null) throw new IllegalArgumentException("Money cannot be null");
        if (this.amount < other.amount) throw new IllegalArgumentException("Resulting amount cannot be negative");

        return new Money(this.amount - other.amount);
    }

    public Money absoluteSubtract(Money other) {
        if (other == null) throw new IllegalArgumentException("Money cannot be null");

        return new Money(Math.abs(this.amount - other.amount));
    }

    public Money add(Money other) {
        if (other == null) throw new IllegalArgumentException("Money cannot be null");

        return new Money(this.amount + other.amount);
    }

    public Money multiply(BigDecimal factor) {
        if (factor == null) throw new IllegalArgumentException("Factor cannot be null");
        if (factor.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Factor cannot be negative");

        return new Money(
                BigDecimal.valueOf(this.amount)
                        .multiply(factor)
                        .setScale(0, RoundingMode.HALF_UP)
                        .intValue()
        );
    }

    public Money divide(BigDecimal divisor) {
        if (divisor == null) throw new IllegalArgumentException("Divisor cannot be null");
        if (divisor.equals(BigDecimal.ZERO)) throw new IllegalArgumentException("Cannot divide by zero");
        if (divisor.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Divisor cannot be negative");

        return new Money(
                BigDecimal.valueOf(this.amount)
                        .divide(divisor, 10, RoundingMode.HALF_UP)
                        .setScale(0, RoundingMode.HALF_UP)
                        .intValue()
        );
    }

    public boolean isLessThan(Money other) {
        return this.amount < other.amount;
    }

    public boolean isGreaterThan(Money other) {
        return this.amount > other.amount;
    }

    public boolean isZero() {
        return this.amount == 0;
    }
}
