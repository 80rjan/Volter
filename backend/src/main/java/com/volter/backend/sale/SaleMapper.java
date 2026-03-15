package com.volter.backend.sale;

import com.volter.backend.customer.mapper.CustomerMapper;
import com.volter.backend.item.mapper.ItemMapper;
import com.volter.backend.sale.dto.SaleCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, CustomerMapper.class})
public abstract class SaleMapper {

    public abstract Sale toEntity(SaleCreationRequest request);
}
