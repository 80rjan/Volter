package com.volter.shop.modules.sale.infrastructure.mapper;

import com.volter.shop.modules.customer.infrastructure.mapper.CustomerMapper;
import com.volter.shop.modules.inventory.infrastructure.mapper.ItemMapper;
import com.volter.shop.modules.sale.application.dto.SaleDetailedResponse;
import com.volter.shop.modules.sale.application.dto.SaleResponse;
import com.volter.shop.modules.sale.domain.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, CustomerMapper.class})
public interface SaleMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.fullName")
    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "purchasePrice", expression = "java(sale.getPurchasePrice().amount())")
    @Mapping(target = "salePrice", expression = "java(sale.getSalePrice() == null ? null : sale.getSalePrice().amount())")
    @Mapping(target = "profit", expression = "java(sale.profit())")
    SaleResponse toResponse(Sale sale);

    @Mapping(target = "purchasePrice", expression = "java(sale.getPurchasePrice().amount())")
    @Mapping(target = "salePrice", expression = "java(sale.getSalePrice() == null ? null : sale.getSalePrice().amount())")
    @Mapping(target = "profit", expression = "java(sale.profit())")
    SaleDetailedResponse toDetailedResponse(Sale sale);
}
