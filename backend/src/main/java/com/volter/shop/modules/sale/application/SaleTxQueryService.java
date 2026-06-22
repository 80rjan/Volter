package com.volter.shop.modules.sale.application;

import com.volter.shop.modules.sale.application.dto.SaleDetailedResponse;
import com.volter.shop.modules.sale.domain.repository.SaleRepository;
import com.volter.shop.modules.sale.domain.repository.SaleTransactionRepository;
import com.volter.shop.modules.sale.infrastructure.mapper.SaleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Read-only sale lookup by transaction id, consumed by the transaction module
 * when assembling a transaction's detailed view. Kept separate from
 * {@link SaleService} to avoid a service dependency cycle: it touches only sale
 * repositories/mappers, never the transaction module.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleTxQueryService {

    private final SaleTransactionRepository saleTxRepository;
    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;

    /** The detailed sale behind a SALE transaction, if any. */
    public Optional<SaleDetailedResponse> findDetailByTransactionId(Long transactionId) {
        return saleTxRepository.findSaleIdByTransactionId(transactionId)
                .flatMap(saleRepository::findById)
                .map(saleMapper::toDetailedResponse);
    }
}
