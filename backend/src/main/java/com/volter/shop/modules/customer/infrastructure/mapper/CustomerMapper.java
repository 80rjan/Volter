package com.volter.shop.modules.customer.infrastructure.mapper;

import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.customer.web.response.CustomerResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CustomerMapper {

    public abstract CustomerResponse toDTO(Customer customer);

}
