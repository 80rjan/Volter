package com.volter.shop.modules.sale.application;

import com.volter.shop.modules.sale.application.dto.SaleDetailedResponse;
import com.volter.shop.modules.sale.domain.repository.SaleRepository;
import com.volter.shop.modules.sale.domain.repository.SaleTransactionRepository;
import com.volter.shop.modules.sale.infrastructure.mapper.SaleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    /** Maps each of the given transaction ids that is a SALE transaction to its customer's name. */
    public Map<Long, String> findCustomerNamesByTransactionIds(Collection<Long> transactionIds) {
        if (transactionIds.isEmpty()) return Map.of();
        Map<Long, String> names = new HashMap<>();
        for (Object[] row : saleTxRepository.findCustomerNamesByTransactionIds(transactionIds)) {
            names.put((Long) row[0], (String) row[1]);
        }
        return names;
    }

    /** Ids of SALE transactions whose customer's name matches the query. */
    public Set<Long> findTransactionIdsByCustomerName(String name) {
        return new HashSet<>(saleTxRepository.findTransactionIdsByCustomerName(name));
    }

    /** Sale margin a staff member has generated since the given moment (bonus base). */
    public long marginForStaffSince(Long staffId, OffsetDateTime since) {
        return saleTxRepository.marginForStaffSince(staffId, since);
    }
}
