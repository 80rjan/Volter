package com.volter.shop.modules.inventory.application.dtos.baseitem.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.volter.shop.modules.inventory.domain.model.enums.ItemReferenceStrategy;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "referenceStrategy"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExistingItemReferenceRequest.class, name = ItemReferenceStrategy.EXISTING_VALUE),
        @JsonSubTypes.Type(value = NewItemReferenceRequest.class, name = ItemReferenceStrategy.NEW_VALUE)
})
public interface ItemReferenceRequest {
    ItemReferenceStrategy getReferenceStrategy();
}
