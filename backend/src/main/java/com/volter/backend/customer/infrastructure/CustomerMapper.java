package com.volter.backend.customer.infrastructure;

import com.volter.backend.customer.domain.model.Customer;
import com.volter.backend.customer.application.dto.CustomerCreationRequest;
import com.volter.backend.customer.application.dto.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CustomerMapper {

    public abstract CustomerDTO toDTO(Customer customer);

    public abstract Customer toEntity(CustomerCreationRequest request);
}
