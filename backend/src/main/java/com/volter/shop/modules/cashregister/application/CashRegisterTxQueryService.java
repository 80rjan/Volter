package com.volter.shop.modules.cashregister.application;

import com.volter.shop.modules.cashregister.application.dto.CashRegisterSessionResponse;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterSessionRepository;
import com.volter.shop.modules.cashregister.infrastructure.mapper.CashRegisterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Read-only cash register session lookup, consumed by the transaction module
 * when assembling a CASH_REGISTER transaction's detailed view. Kept separate
 * from {@link CashRegisterService} to avoid a service dependency cycle: it
 * touches only the session repository/mapper.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashRegisterTxQueryService {

    private final CashRegisterSessionRepository sessionRepository;
    private final CashRegisterMapper cashRegisterMapper;

    /** The session a transaction belongs to, with its parent register pre-fetched. */
    public Optional<CashRegisterSessionResponse> findSessionDetail(Long sessionId) {
        return sessionRepository.findById(sessionId).map(cashRegisterMapper::toResponse);
    }
}
