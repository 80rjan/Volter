package com.volter.shop.modules.pawn.domain.model.valueobject;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public record PawnPeriod(
        LocalDate issueDate,
        LocalDate maturityDate
) {
    public PawnPeriod {
        if (issueDate == null || maturityDate == null) {
            throw new IllegalArgumentException("Issue date and/or maturity date cannot be null");
        }
        if (maturityDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Maturity date cannot be before issue date");
        }
    }

    public PawnPeriod extend(int plusDays) {
        if (plusDays < 0) {
            throw new IllegalArgumentException("Plus days must be a positive integer");
        }
        LocalDate newMaturityDate = maturityDate.plusDays(plusDays);

        return new PawnPeriod(issueDate, newMaturityDate);
    }

    public boolean isMatured() {
        return LocalDate.now().isAfter(maturityDate);
    }

    public boolean isMaturingToday() {
        return LocalDate.now().isEqual(maturityDate);
    }
}
