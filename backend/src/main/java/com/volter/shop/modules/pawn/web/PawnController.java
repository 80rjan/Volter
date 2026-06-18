package com.volter.shop.modules.pawn.web;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.pawn.application.PawnService;
import com.volter.shop.modules.pawn.application.dto.*;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.infrastructure.mapper.PawnContractMapper;
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

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing pawn contracts.
 * Pawn contracts of each status can be returned, depending on the status filter (no restrictions).
 * Pawn contracts are returned regardless of who created them, because they're per shop (no restrictions).
 */
@RestController
@RequestMapping("/pawns")
@RequiredArgsConstructor
public class PawnController {

    private final PawnService pawnService;
    private final PawnContractMapper pawnContractMapper;
    private final StaffService staffService;

    /**
     * List pawn contracts with optional filters.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('PAWN_READ')")
    public ResponseEntity<PageResponse<PawnContractResponse>> list(@ModelAttribute PawnFilterRequest filter, Pageable pageable) {
        var page = pawnService.list(filter, pageable);
        Map<Long, String> staffNames = staffService.findStaffNames(
                page.stream().map(PawnContract::getCreatedByStaffId).collect(Collectors.toSet()));
        return ResponseEntity.ok(PageResponse.of(page,
                c -> pawnContractMapper.toResponse(c, staffNames.get(c.getCreatedByStaffId()))));
    }

    /**
     * Get details of a specific pawn contract by its ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAWN_READ')")
    public ResponseEntity<PawnContractDetailedResponse> get(@PathVariable Long id) {
        PawnContract contract = pawnService.get(id);
        return ResponseEntity.ok(pawnContractMapper.toDetailedResponse(contract, staffNameOf(contract)));
    }

    /**
     * Create a pawn contract.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('PAWN_WRITE')")
    public ResponseEntity<PawnContractResponse> create(@Valid @RequestBody PawnCreateRequest request,
                                                       @AuthenticationPrincipal StaffPrincipal principal) {
        PawnContract contract = pawnService.create(request, principal.staffId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pawnContractMapper.toResponse(contract, staffNameOf(contract)));
    }

    /**
     * Extend a pawn contract.
     */
    @PostMapping("/{id}/extend")
    @PreAuthorize("hasAuthority('PAWN_WRITE')")
    public ResponseEntity<PawnContractExtensionResponse> extend(@PathVariable Long id,
                                                                @Valid @RequestBody PawnExtendRequest request,
                                                                @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(pawnContractMapper.toResponse(pawnService.extend(id, request, principal.staffId())));
    }

    /**
     * Redeem a pawn contract.
     */
    @PostMapping("/{id}/redeem")
    @PreAuthorize("hasAuthority('PAWN_WRITE')")
    public ResponseEntity<PawnContractResponse> redeem(@PathVariable Long id,
                                                       @Valid @RequestBody PawnRedeemRequest request,
                                                       @AuthenticationPrincipal StaffPrincipal principal) {
        PawnContract contract = pawnService.redeem(id, request, principal.staffId());
        return ResponseEntity.ok(pawnContractMapper.toResponse(contract, staffNameOf(contract)));
    }

    /**
     * Forfeit a pawn contract.
     */
    @PostMapping("/{id}/forfeit")
    @PreAuthorize("hasAuthority('PAWN_FORFEIT')")
    public ResponseEntity<PawnContractResponse> forfeit(@PathVariable Long id,
                                                        @AuthenticationPrincipal StaffPrincipal principal) {
        PawnContract contract = pawnService.forfeit(id, principal.staffId());
        return ResponseEntity.ok(pawnContractMapper.toResponse(contract, staffNameOf(contract)));
    }

    private String staffNameOf(PawnContract contract) {
        return staffService.findStaffNames(Set.of(contract.getCreatedByStaffId())).get(contract.getCreatedByStaffId());
    }
}
