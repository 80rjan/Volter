package com.volter.shop.modules.transaction.api;

import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.application.dto.filter.TransactionFilter;
import com.volter.shop.modules.transaction.application.dto.response.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.application.dto.response.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.infrastructure.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("{api.base.path}/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAll(TransactionFilter filters, Pageable pageable) {
        Page<Transaction> transactions = transactionService.getAll(filters, pageable);
        return ResponseEntity.ok(transactions.map(transactionMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDetailedResponse> getById(@PathVariable Long id) {
        Transaction transaction = transactionService.getById(id);
        return ResponseEntity.ok(transactionMapper.toDetailedResponse(transaction));
    }
}
