package com.volter.shop.modules.inventory.domain.model.types;

import com.volter.shop.modules.inventory.domain.model.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@DiscriminatorValue("OTHER")
public class OtherItem extends Item {

    @NotBlank(message = "Other item category is required")
    @Column(nullable = false)
    private String category;
}
