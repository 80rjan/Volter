package com.volter.shop.shared.valueobject;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
public record Compensation(
        Money baseSalary,
        BigDecimal bonusPercent
) {
    public Compensation {
        if (baseSalary.isLessThan(new Money(0))) {
            throw new IllegalArgumentException("Base salary cannot be negative");
        }
        if (bonusPercent.compareTo(BigDecimal.ZERO) < 0 || bonusPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Bonus percent must be between 0 and 100");
        }
    }

    public Integer getBonusAmount() {
        return baseSalary.multiply(
                bonusPercent.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        ).amount();
    }

    public Integer calculateTotalCompensation() {
        return baseSalary.amount() + getBonusAmount();
    }
}
