package com.volter.shop.modules.sale.infrastructure;

import com.volter.shop.modules.customer.infrastructure.CustomerMapper;
import com.volter.shop.modules.inventory.infrastructure.ItemDetailedMapper;
import com.volter.shop.modules.inventory.infrastructure.ItemMapper;
import com.volter.shop.modules.sale.application.dto.response.SaleDetailedResponse;
import com.volter.shop.modules.sale.application.dto.response.SaleResponse;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.shared.valueobject.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, ItemDetailedMapper.class, CustomerMapper.class})
public abstract class SaleMapper {

    @Mapping(source = "item", target = "item", qualifiedByName = "toBaseResponse")
    public abstract SaleResponse toResponse(Sale sale);
    public abstract List<SaleResponse> toResponse(List<Sale> sales);

    @Mapping(source = "item", target = "item", qualifiedByName = "toDetailedResponse")
    public abstract SaleDetailedResponse toDetailedResponse(Sale sale);
    public abstract List<SaleDetailedResponse> toDetailedResponse(List<Sale> sales);

    protected Integer map(Money money) {
        return money != null ? money.amount() : null;
    }
}
