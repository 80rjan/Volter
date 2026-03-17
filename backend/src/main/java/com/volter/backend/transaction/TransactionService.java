package com.volter.backend.transaction;

import lombok.RequiredArgsConstructor;
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
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    @Transactional
    public List<Transaction> getByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }
}
