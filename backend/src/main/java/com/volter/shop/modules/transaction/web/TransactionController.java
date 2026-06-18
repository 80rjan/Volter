package com.volter.shop.modules.transaction.web;

import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.application.dto.TransactionFilterRequest;
import com.volter.shop.modules.transaction.application.dto.TransactionResponse;
import com.volter.shop.modules.transaction.infrastructure.mapper.TransactionMapper;
import com.volter.shared.security.StaffPrincipal;
import com.volter.shared.web.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for transaction reading operations.
 * Allows staff members to view their own transactions and those of their subordinates (recursively), with appropriate permissions.
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('TRANSACTION_READ')")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    /**
     * List transactions made by the caller or by staff members below them (recursively), with optional filters.
     */
    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> list(@ModelAttribute TransactionFilterRequest filter, Pageable pageable,
                                                                  @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(PageResponse.of(
                transactionService.list(filter, pageable, principal.staffId()), transactionMapper::toResponse));
    }

    /**
     * Get details of a specific transaction (own or one made by a subordinate).
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> get(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(transactionMapper.toResponse(transactionService.get(id, principal.staffId())));
    }
}
