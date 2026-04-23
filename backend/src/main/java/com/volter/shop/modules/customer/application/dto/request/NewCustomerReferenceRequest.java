package com.volter.shop.modules.customer.application.dto.request;

import com.volter.shop.modules.customer.domain.model.enums.CustomerReferenceStrategy;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class NewCustomerReferenceRequest implements CustomerReferenceRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String phoneNumber;

    // could be blank or null
    private String reservePhoneNumber;

    @NotBlank(message = "EMBG is required")
    private String embg;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @Override
    public CustomerReferenceStrategy getReferenceStrategy() {
        return CustomerReferenceStrategy.NEW;
    }
}
