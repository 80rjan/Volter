package com.volter.shop.modules.customer.infrastructure;

import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.customer.application.dto.response.CustomerResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CustomerMapper {

    public abstract CustomerResponse toDTO(Customer customer);

}
