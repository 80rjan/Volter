package com.volter.shop.modules.customer.infrastructure.mapper;

import com.volter.shop.modules.customer.application.dto.CustomerResponse;
import com.volter.shop.modules.customer.domain.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toResponse(Customer customer);
}
