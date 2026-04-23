package com.volter.shop.modules.pawn.domain.model.valueobject;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public record MaturityDateChange(
        LocalDate oldMaturityDate,
        LocalDate newMaturityDate
) {
    public MaturityDateChange {
        if (oldMaturityDate == null || newMaturityDate == null) {
            throw new IllegalArgumentException("Maturity dates cannot be null");
        }
        if (newMaturityDate.isBefore(oldMaturityDate)) {
            throw new IllegalArgumentException("New maturity date cannot be before the old maturity date");
        }
    }
}
