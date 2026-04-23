package com.volter.shop.modules.inventory.application.dtos.types.watch;

import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemDetailedResponseData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WatchItemDetailedResponseData extends WatchItemResponseData
        implements ItemDetailedResponseData {

    @NotNull(message = "Item status is required")
    private ItemStatus itemStatus;

    @NotNull(message = "Created at timestamp is required")
    private LocalDateTime createdAt;

    @NotNull(message = "Updated at timestamp is required")
    private LocalDateTime updatedAt;


    @NotNull(message = "Watch is original box included is required")
    private boolean originalBoxIncluded;

    @NotNull(message = "Watch is original papers included is required")
    private boolean originalPapersIncluded;

    @NotNull(message = "Watch is warranty card included is required")
    private boolean warrantyCardIncluded;

    // nullable
    private LocalDate warrantyExpirationDate;

    @NotNull(message = "Watch is functional is required")
    private boolean functional;

    @NotNull(message = "Watch is service required is required")
    private boolean serviceRequired;

}
