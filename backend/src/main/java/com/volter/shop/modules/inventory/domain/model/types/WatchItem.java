package com.volter.shop.modules.inventory.domain.model.types;

import com.volter.shop.modules.inventory.domain.model.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@DiscriminatorValue("WATCH")
public class WatchItem extends Item {

    @NotBlank(message = "Watch brand is required")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Watch model is required")
    @Column(nullable = false)
    private String model;

    @NotBlank(message = "Watch material is required")
    @Column(nullable = false)
    private String material;

    @NotNull(message = "Watch year is required")
    @Column(nullable = false)
    private Integer year;

    @NotNull(message = "Watch is original box included is required")
    @Column(nullable = false)
    private boolean originalBoxIncluded;

    @NotNull(message = "Watch is original papers included is required")
    @Column(nullable = false)
    private boolean originalPapersIncluded;

    @NotNull(message = "Watch is warranty card included is required")
    @Column(nullable = false)
    private boolean warrantyCardIncluded;

    @Column(nullable = true)
    private LocalDate warrantyExpirationDate;

    @NotNull(message = "Watch is functional is required")
    @Column(nullable = false)
    private boolean functional;

    @NotNull(message = "Watch is service required is required")
    @Column(nullable = false)
    private boolean serviceRequired;
}
