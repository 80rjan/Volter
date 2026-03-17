package com.volter.backend.transaction;

import com.volter.backend.transaction.dto.TransactionFilterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Page<Transaction> getAll(TransactionFilterDTO filters, Pageable pageable) {
        Specification<Transaction> spec = TransactionSpecification.withFilters(filters);
        return transactionRepository.findAll(spec, pageable);
    }

    @Transactional
    public List<Transaction> getByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }
}
