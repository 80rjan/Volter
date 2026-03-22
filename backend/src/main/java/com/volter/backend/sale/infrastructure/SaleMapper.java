package com.volter.backend.sale.infrastructure;

import com.volter.backend.customer.infrastructure.CustomerMapper;
import com.volter.backend.item.infrastructure.ItemMapper;
import com.volter.backend.sale.domain.model.Sale;
import com.volter.backend.sale.application.dto.SaleCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, CustomerMapper.class})
public abstract class SaleMapper {

    public abstract Sale toEntity(SaleCreationRequest request);
}
