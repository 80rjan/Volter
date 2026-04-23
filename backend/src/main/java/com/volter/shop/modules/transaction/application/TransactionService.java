package com.volter.shop.modules.transaction.application;

import com.volter.shop.modules.transaction.domain.repository.TransactionSpecification;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.repository.TransactionRepository;
import com.volter.shop.modules.transaction.application.dto.filter.TransactionFilter;
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
    public Page<Transaction> getAll(TransactionFilter filters, Pageable pageable) {
        Specification<Transaction> spec = TransactionSpecification.withFilters(filters);
        return transactionRepository.findAll(spec, pageable);
    }

    @Transactional
    public Transaction getById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    @Transactional
    public List<Transaction> getByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }
}
