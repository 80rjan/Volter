package com.volter.backend.customer.mapper;

import com.volter.backend.customer.Customer;
import com.volter.backend.customer.dto.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CustomerMapper {

    public abstract CustomerDTO toDTO(Customer customer);
}
