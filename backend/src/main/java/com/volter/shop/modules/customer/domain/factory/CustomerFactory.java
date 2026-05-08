package com.volter.shop.modules.customer.domain.factory;

import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.web.request.CustomerReferenceRequest;
import com.volter.shop.modules.customer.web.request.ExistingCustomerReferenceRequest;
import com.volter.shop.modules.customer.web.request.NewCustomerReferenceRequest;
import com.volter.shop.modules.customer.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomerFactory {

    private final CustomerService customerService;

    public Customer createOrGetCustomer(CustomerReferenceRequest request) {
        return switch (request.getReferenceStrategy())  {
            case EXISTING -> getExistingCustomer((ExistingCustomerReferenceRequest) request);
            case NEW -> createNewCustomer((NewCustomerReferenceRequest) request);
        };
    }

    private Customer getExistingCustomer(ExistingCustomerReferenceRequest request) {
        return customerService.getById(request.getCustomerId());
    }

    private Customer createNewCustomer(NewCustomerReferenceRequest request) {
        return Customer.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .reservePhoneNumber(request.getReservePhoneNumber())
                .embg(request.getEmbg())
                .address(request.getAddress())
                .city(request.getCity())
                .build();
    }
}
