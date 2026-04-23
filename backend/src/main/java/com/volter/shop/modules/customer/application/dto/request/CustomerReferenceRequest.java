package com.volter.shop.modules.customer.application.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.volter.shop.modules.customer.domain.model.enums.CustomerReferenceStrategy;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "referenceStrategy"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExistingCustomerReferenceRequest.class, name = CustomerReferenceStrategy.EXISTING_VALUE),
        @JsonSubTypes.Type(value = NewCustomerReferenceRequest.class, name = CustomerReferenceStrategy.NEW_VALUE)
})
public interface CustomerReferenceRequest {
    CustomerReferenceStrategy getReferenceStrategy();
}
