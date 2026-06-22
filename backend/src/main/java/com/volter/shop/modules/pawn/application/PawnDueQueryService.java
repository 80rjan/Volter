package com.volter.shop.modules.pawn.application;

import com.volter.shop.modules.pawn.application.dto.PawnFilterRequest;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.enums.PawnContractStatus;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.pawn.domain.specification.PawnContractSpecification;
import com.volter.shop.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Read-only pawn queries consumed across module boundaries. Kept separate from
 * {@link PawnService} so other modules (e.g. cash register) can read pawn data
 * without forming a service dependency cycle: this component depends only on the
 * pawn repository, never on another module's service.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PawnDueQueryService {

    private final PawnContractRepository contractRepository;

    /**
     * Total interest amount across active pawn contracts that are already due
     * (due date today or in the past) plus those coming due within the next
     * {@code days} days. Used to seed a cash register session's expected interest.
     */
    public Money totalInterestDue(int days) {
        return new Money(
                listDueInDays(days).stream().mapToInt(p -> p.getInterestAmount().amount()).sum()
        );
    }

    /**
     * Active pawn contracts whose due date is on or before {@code today + days}:
     * everything already overdue or due today, plus anything coming due within the
     * window. No lower bound, so past-due contracts are included. Only ACTIVE
     * contracts count — redeemed/forfeited ones are already settled.
     */
    private List<PawnContract> listDueInDays(int days) {
        return contractRepository.findAll(PawnContractSpecification.matches(
                PawnFilterRequest.builder()
                        .status(PawnContractStatus.ACTIVE)
                        .dueTo(LocalDate.now().plusDays(days))
                        .build()
        ));
    }
}
