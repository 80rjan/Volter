package com.volter.shop.modules.cashregister.web;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.identity.modules.staff.infrastructure.mapper.StaffMapper;
import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.application.dto.*;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.infrastructure.mapper.CashRegisterMapper;
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

/**
 * REST controller for managing cash registers and sessions.
 * Allow staff members to view and manage their own cash register sessions and those of their subordinates (recursively).
 * Allow staff members to view and manage their own cash register session discrepancies and those of their subordinates (recursively).
 */
@RestController
@RequiredArgsConstructor
public class CashRegisterController {

    private final CashRegisterService cashRegisterService;
    private final CashRegisterMapper cashRegisterMapper;
    private final StaffService staffService;
    private final StaffMapper staffMapper;

    // ----- registers -----

    /**
     * List all cash registers.
     */
    @GetMapping("/cash-registers")
    @PreAuthorize("hasAuthority('CASH_REGISTER_READ')")
    public ResponseEntity<List<CashRegisterResponse>> listRegisters() {
        return ResponseEntity.ok(cashRegisterService.listRegisters().stream().map(cashRegisterMapper::toResponse).toList());
    }

    /**
     * Create a new cash register.
     */
    @PostMapping("/cash-registers")
    @PreAuthorize("hasAuthority('CASH_REGISTER_MANAGE')")
    public ResponseEntity<CashRegisterResponse> createRegister(@Valid @RequestBody CashRegisterCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cashRegisterMapper.toResponse(cashRegisterService.createRegister(request)));
    }

    // ----- sessions -----

    /**
     * List sessions operated by the staff member and those of his subordinates (recursively).
     */
    @GetMapping("/cash-register-sessions")
    @PreAuthorize("hasAuthority('CASH_REGISTER_SESSION_READ')")
    public ResponseEntity<PageResponse<CashRegisterSessionResponse>> listSessions(
            @ModelAttribute SessionFilterRequest filter, Pageable pageable,
            @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(PageResponse.of(
                cashRegisterService.listSessions(principal.staffId(), filter, pageable), cashRegisterMapper::toResponse));
    }

    /**
     * Get details of a session.
     */
    @GetMapping("/cash-register-sessions/{id}")
    @PreAuthorize("hasAuthority('CASH_REGISTER_SESSION_READ')")
    public ResponseEntity<CashRegisterSessionResponse> getSession(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(cashRegisterMapper.toResponse(cashRegisterService.getSession(id, principal.staffId())));
    }

    /**
     * Get a session together with the staff member who operated it.
     */
    @GetMapping("/cash-register-sessions/{id}/detailed")
    @PreAuthorize("hasAuthority('CASH_REGISTER_SESSION_READ')")
    public ResponseEntity<CashRegisterSessionDetailedResponse> getSessionDetailed(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        CashRegisterSession session = cashRegisterService.getSession(id, principal.staffId());
        Staff operator = staffService.get(session.getStaffId(), principal.staffId());
        return ResponseEntity.ok(new CashRegisterSessionDetailedResponse(
                cashRegisterMapper.toResponse(session),
                staffMapper.toResponse(operator)));
    }

    /**
     * Open a new cash register session for the given register, operated by the caller.
     */
    @PostMapping("/cash-registers/{registerId}/open-session")
    @PreAuthorize("hasAuthority('CASH_REGISTER_SESSION_MANAGE')")
    public ResponseEntity<CashRegisterSessionResponse> openSession(@PathVariable Long registerId,
                                                                   @Valid @RequestBody SessionOpenRequest request,
                                                                   @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cashRegisterMapper.toResponse(cashRegisterService.openSession(registerId, request, principal.staffId())));
    }

    /**
     * Close a cash register session for the given cash register.
     * Only the staff member who opened the session can close it.
     */
    @PostMapping("/cash-registers/{registerId}/close-session")
    @PreAuthorize("hasAuthority('CASH_REGISTER_SESSION_MANAGE')")
    public ResponseEntity<CashRegisterSessionResponse> closeSession(@PathVariable Long registerId,
                                                                    @Valid @RequestBody SessionCloseRequest request,
                                                                    @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(cashRegisterMapper.toResponse(cashRegisterService.closeSession(registerId, request, principal.staffId())));
    }

    /**
     * Record a transaction (cash in or out) for a cash register session.
     */
    @PostMapping("/cash-register-sessions/{id}/record-transaction")
    @PreAuthorize("hasAuthority('CASH_REGISTER_SESSION_WRITE')")
    public ResponseEntity<CashRegisterTransactionResponse> recordTransaction(@PathVariable Long id,
                                                                             @Valid @RequestBody CashRegisterTransactionRecordRequest request,
                                                                             @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cashRegisterMapper.toResponse(cashRegisterService.recordTransaction(id, request, principal.staffId())));
    }

    // ----- discrepancies -----

    /**
     * List discrepancies which were created in sessions operated by the staff member or those of his subordinates (recursively).
     */
    @GetMapping("/cash-register-session-discrepancies")
    @PreAuthorize("hasAuthority('CASH_REGISTER_SESSION_DISCREPANCY_READ')")
    public ResponseEntity<PageResponse<DiscrepancyResponse>> listDiscrepancies(
            @ModelAttribute DiscrepancyFilterRequest filter, Pageable pageable,
            @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(PageResponse.of(
                cashRegisterService.listDiscrepancies(principal.staffId(), filter, pageable), cashRegisterMapper::toResponse));
    }

    /**
     * Resolve a discrepancy. The adjustment is made by the manager of the staff member who made the discrepancy.
     */
    @PostMapping("/cash-register-session-discrepancies/{id}/resolve")
    @PreAuthorize("hasAuthority('CASH_REGISTER_SESSION_DISCREPANCY_RESOLVE')")
    public ResponseEntity<DiscrepancyResponse> resolveDiscrepancy(@PathVariable Long id,
                                                                  @Valid @RequestBody DiscrepancyResolveRequest request,
                                                                  @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(cashRegisterMapper.toResponse(cashRegisterService.resolveDiscrepancy(id, request, principal.staffId())));
    }
}
