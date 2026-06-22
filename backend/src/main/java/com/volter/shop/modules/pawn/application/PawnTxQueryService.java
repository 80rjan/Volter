package com.volter.shop.modules.pawn.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.pawn.application.dto.PawnContractDetailedResponse;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.pawn.infrastructure.mapper.PawnContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Read-only pawn lookup by transaction id, consumed by the transaction module
 * when assembling a transaction's detailed view. Kept separate from
 * {@link PawnService} (which depends on the transaction module) so this never
 * forms a service dependency cycle: it touches only pawn repositories/mappers.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PawnTxQueryService {

    private final PawnTransactionRepository pawnTxRepository;
    private final PawnContractRepository contractRepository;
    private final PawnContractMapper pawnContractMapper;
    private final StaffService staffService;

    /** The detailed pawn contract behind a PAWN transaction, if any. */
    public Optional<PawnContractDetailedResponse> findDetailByTransactionId(Long transactionId) {
        return pawnTxRepository.findPawnContractIdByTransactionId(transactionId)
                .flatMap(contractRepository::findById)
                .map(contract -> pawnContractMapper.toDetailedResponse(
                        contract,
                        staffService.findStaffNames(Set.of(contract.getCreatedByStaffId()))
                                .get(contract.getCreatedByStaffId())));
    }
}
