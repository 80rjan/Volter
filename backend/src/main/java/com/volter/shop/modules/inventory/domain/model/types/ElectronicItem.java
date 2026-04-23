package com.volter.shop.modules.inventory.domain.model.types;

import com.volter.shop.modules.inventory.domain.model.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@DiscriminatorValue("ELECTRONIC")
public class ElectronicItem extends Item{

    @NotBlank(message = "Electronic brand is required")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Electronic category is required")
    @Column(nullable = false)
    private String category;

    @NotNull(message = "Electronic year is required")
    @Column(nullable = false)
    private Integer year;
}
