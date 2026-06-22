package com.volter.shop.modules.expense.web;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.expense.application.ExpenseService;
import com.volter.shop.modules.expense.application.dto.ExpenseCreateRequest;
import com.volter.shop.modules.expense.application.dto.ExpenseFilterRequest;
import com.volter.shop.modules.expense.application.dto.ExpenseMonthlySummary;
import com.volter.shop.modules.expense.application.dto.ExpenseResponse;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.modules.expense.infrastructure.mapper.ExpenseMapper;
import com.volter.shared.security.StaffPrincipal;
import com.volter.shared.web.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing expenses.
 * Allows staff members to view their own expenses and those of their subordinates (recursively).
 */
@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseMapper expenseMapper;
    private final StaffService staffService;

    /**
     * List expenses the caller may see, with optional filters.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('EXPENSE_READ')")
    public ResponseEntity<PageResponse<ExpenseResponse>> list(@ModelAttribute ExpenseFilterRequest filter, Pageable pageable,
                                                              @AuthenticationPrincipal StaffPrincipal principal) {
        var page = expenseService.list(filter, pageable, principal.staffId());
        Map<Long, String> staffNames = staffService.findStaffNames(
                page.stream().map(Expense::getStaffId).collect(Collectors.toSet()));
        return ResponseEntity.ok(PageResponse.of(page,
                e -> expenseMapper.toResponse(e, staffNames.get(e.getStaffId()))));
    }

    /**
     * Expenses the caller may see, grouped by month with per-category totals.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('EXPENSE_READ')")
    public ResponseEntity<List<ExpenseMonthlySummary>> summary(@ModelAttribute ExpenseFilterRequest filter,
                                                               @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(expenseService.summarizeByMonth(filter, principal.staffId()));
    }

    /**
     * Fetch an expense.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EXPENSE_READ')")
    public ResponseEntity<ExpenseResponse> get(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        Expense expense = expenseService.get(id, principal.staffId());
        return ResponseEntity.ok(expenseMapper.toResponse(expense, staffNameOf(expense)));
    }

    /**
     * Create a new expense.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('EXPENSE_WRITE')")
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseCreateRequest request,
                                                  @AuthenticationPrincipal StaffPrincipal principal) {
        Expense expense = expenseService.record(request, principal.staffId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseMapper.toResponse(expense, staffNameOf(expense)));
    }

    private String staffNameOf(Expense expense) {
        return staffService.findStaffNames(Set.of(expense.getStaffId())).get(expense.getStaffId());
    }
}
