package com.volter.shop.modules.transaction.web;

import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.application.dto.MonthlyProfitResponse;
import com.volter.shop.modules.transaction.application.dto.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.application.dto.TransactionFilterRequest;
import com.volter.shop.modules.transaction.application.dto.TransactionResponse;
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

    /**
     * List transactions made by the caller or by staff members below them (recursively), with optional filters.
     */
    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> list(@ModelAttribute TransactionFilterRequest filter, Pageable pageable,
                                                                  @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(PageResponse.of(
                transactionService.list(filter, pageable, principal.staffId())));
    }

    /**
     * Profit earned from the first of the current month until now, derived from
     * the transaction ledger: pawn provision, sale profit, and their sum. Shown
     * as a dedicated month-to-date profit bar on the pawns and sales pages.
     */
    @GetMapping("/monthly-profit")
    @PreAuthorize("hasAuthority('PROFIT_READ')")
    public ResponseEntity<MonthlyProfitResponse> monthlyProfit() {
        return ResponseEntity.ok(transactionService.monthlyProfit());
    }

    /**
     * Get the full detailed view of a specific transaction (own or one made by a
     * subordinate): ledger data, the staff member who made it, and the matching
     * type-specific block (pawn / sale / expense / cash register session).
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDetailedResponse> get(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(transactionService.getDetailed(id, principal.staffId()));
    }

    /**
     * The detailed view of a non-monetary activity row (a pawn contract event, such
     * as a forfeiture). Same shape as a transaction's detail, with the money fields
     * null — the list marks these rows with kind = PAWN_EVENT.
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<TransactionDetailedResponse> getEvent(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(transactionService.getEventDetailed(id, principal.staffId()));
    }
}
