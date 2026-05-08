package com.volter.shop.modules.customer.web.request;

import com.volter.shop.modules.customer.domain.model.enums.CustomerReferenceStrategy;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ExistingCustomerReferenceRequest implements CustomerReferenceRequest {

    @NotNull(message = "Customer ID is required for existing customer reference")
    private Long customerId;

    @Override
    public CustomerReferenceStrategy getReferenceStrategy() {
        return CustomerReferenceStrategy.EXISTING;
    }
}
