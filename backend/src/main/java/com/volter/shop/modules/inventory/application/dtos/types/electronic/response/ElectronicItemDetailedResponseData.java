package com.volter.shop.modules.inventory.application.dtos.types.electronic.response;

import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemDetailedResponseData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElectronicItemDetailedResponseData extends ElectronicItemResponseData
        implements ItemDetailedResponseData {

    @NotNull(message = "Item status is required")
    private ItemStatus itemStatus;

    @NotNull(message = "Created at timestamp is required")
    private LocalDateTime createdAt;

    @NotNull(message = "Updated at timestamp is required")
    private LocalDateTime updatedAt;

}
