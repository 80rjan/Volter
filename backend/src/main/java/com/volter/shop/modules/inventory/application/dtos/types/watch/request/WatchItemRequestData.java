package com.volter.shop.modules.inventory.application.dtos.types.watch.request;

import com.volter.shop.modules.inventory.application.dtos.baseitem.request.ItemRequestData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WatchItemRequestData implements ItemRequestData {

    @NotNull(message = "Item origin type is required")
    private ItemOriginType itemOriginType;

    @NotNull(message = "Item status is required")
    private ItemStatus itemStatus;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Material is required")
    private String material;

    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    private Integer year;

    @NotNull(message = "Original box included flag is required")
    private Boolean originalBoxIncluded;

    @NotNull(message = "Original papers included flag is required")
    private Boolean originalPapersIncluded;

    @NotNull(message = "Warranty card included flag is required")
    private Boolean warrantyCardIncluded;

    private LocalDate warrantyExpirationDate;

    @NotNull(message = "Functional flag is required")
    private Boolean functional;

    @NotNull(message = "Service required flag is required")
    private Boolean serviceRequired;

    @Override
    public ItemType getItemType() {
        return ItemType.WATCH;
    }
}