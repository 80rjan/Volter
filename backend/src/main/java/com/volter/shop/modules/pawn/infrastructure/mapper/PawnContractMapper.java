package com.volter.shop.modules.pawn.infrastructure.mapper;

import com.volter.shop.modules.customer.infrastructure.mapper.CustomerMapper;
import com.volter.shop.modules.inventory.infrastructure.mapper.ItemMapper;
import com.volter.shop.modules.pawn.application.dto.PawnContractDetailedResponse;
import com.volter.shop.modules.pawn.application.dto.PawnContractExtensionResponse;
import com.volter.shop.modules.pawn.application.dto.PawnContractResponse;
import com.volter.shop.modules.pawn.application.dto.PawnContractNoteResponse;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.PawnContractExtension;
import com.volter.shop.modules.pawn.domain.model.PawnContractNote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, CustomerMapper.class})
public interface PawnContractMapper {

    @Mapping(target = "customerId", source = "contract.customer.id")
    @Mapping(target = "customerName", source = "contract.customer.fullName")
    @Mapping(target = "createdByStaffName", source = "createdByStaffName")
    @Mapping(target = "principalAmount", expression = "java(contract.getPrincipalAmount().amount())")
    @Mapping(target = "interestAmount", expression = "java(contract.getInterestAmount().amount())")
    @Mapping(target = "daysOverdue", expression = "java(contract.daysOverdue())")
    PawnContractResponse toResponse(PawnContract contract, String createdByStaffName);

    // Notes come in as their own argument: they are loaded separately rather than
    // fetched with the contract, which already fetches the `extensions` list.
    @Mapping(target = "createdByStaffName", source = "createdByStaffName")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "principalAmount", expression = "java(contract.getPrincipalAmount().amount())")
    @Mapping(target = "interestAmount", expression = "java(contract.getInterestAmount().amount())")
    @Mapping(target = "daysOverdue", expression = "java(contract.daysOverdue())")
    PawnContractDetailedResponse toDetailedResponse(PawnContract contract, String createdByStaffName, List<PawnContractNote> notes);

    @Mapping(target = "interestPaid", expression = "java(extension.getInterestPaid().amount())")
    @Mapping(target = "fee", expression = "java(extension.getFee().amount())")
    PawnContractExtensionResponse toResponse(PawnContractExtension extension);

    PawnContractNoteResponse toResponse(PawnContractNote note);
}
