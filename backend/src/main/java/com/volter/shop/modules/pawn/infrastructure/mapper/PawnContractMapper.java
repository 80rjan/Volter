package com.volter.shop.modules.pawn.infrastructure.mapper;

import com.volter.shop.modules.customer.infrastructure.mapper.CustomerMapper;
import com.volter.shop.modules.inventory.infrastructure.mapper.ItemMapper;
import com.volter.shop.modules.pawn.application.dto.PawnContractDetailedResponse;
import com.volter.shop.modules.pawn.application.dto.PawnContractExtensionResponse;
import com.volter.shop.modules.pawn.application.dto.PawnContractResponse;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.PawnContractExtension;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, CustomerMapper.class})
public interface PawnContractMapper {

    @Mapping(target = "customerId", source = "contract.customer.id")
    @Mapping(target = "customerName", source = "contract.customer.fullName")
    @Mapping(target = "createdByStaffName", source = "createdByStaffName")
    @Mapping(target = "principalAmount", expression = "java(contract.getPrincipalAmount().amount())")
    @Mapping(target = "interestAmount", expression = "java(contract.getInterestAmount().amount())")
    @Mapping(target = "daysOverdue", expression = "java(contract.daysOverdue())")
    PawnContractResponse toResponse(PawnContract contract, String createdByStaffName);

    @Mapping(target = "createdByStaffName", source = "createdByStaffName")
    @Mapping(target = "principalAmount", expression = "java(contract.getPrincipalAmount().amount())")
    @Mapping(target = "interestAmount", expression = "java(contract.getInterestAmount().amount())")
    @Mapping(target = "daysOverdue", expression = "java(contract.daysOverdue())")
    PawnContractDetailedResponse toDetailedResponse(PawnContract contract, String createdByStaffName);

    @Mapping(target = "interestPaid", expression = "java(extension.getInterestPaid().amount())")
    @Mapping(target = "fee", expression = "java(extension.getFee().amount())")
    PawnContractExtensionResponse toResponse(PawnContractExtension extension);
}
