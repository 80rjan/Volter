package com.volter.backend.item.domain.model.details;

import com.volter.backend.item.domain.model.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OtherItemDetails {

    @Id
    private Long itemId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_other_item_details_item"))
    private Item item;

    @NotNull(message = "Other item category is required")
    @Column(nullable = false)
    private String category;

    @NotNull(message = "Other item description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}
