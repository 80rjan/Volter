package com.volter.shop.modules.transaction.web;

import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.web.request.TransactionFilterRequest;
import com.volter.shop.modules.transaction.web.response.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.web.response.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.infrastructure.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAll(TransactionFilterRequest filters, Pageable pageable) {
        Page<Transaction> transactions = transactionService.getAll(filters, pageable);
        return ResponseEntity.ok(transactions.map(transactionMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDetailedResponse> getById(@PathVariable Long id) {
        Transaction transaction = transactionService.getById(id);
        return ResponseEntity.ok(transactionMapper.toDetailedResponse(transaction));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TransactionResponse>> getByCustomerId(@PathVariable Long customerId) {
        List<Transaction> transactions = transactionService.getByCustomerId(customerId);
        return ResponseEntity.ok(transactionMapper.toResponse(transactions));
    }
}
