package com.volter.shop.modules.sale.web;

import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.sale.application.dto.*;
import com.volter.shop.modules.sale.infrastructure.mapper.SaleMapper;
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

/**
 * REST endpoint for managing sales.
 * Sales of each status can be returned, depending on the status filter (no restrictions).
 * Also, sales are returned regardless of who created them, because they're per shop (no restrictions).
 */
@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;
    private final SaleMapper saleMapper;

    /**
     * List sales with optional filters.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SALE_READ')")
    public ResponseEntity<PageResponse<SaleResponse>> list(@ModelAttribute SaleFilterRequest filter, Pageable pageable,
                                                           @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(PageResponse.of(saleService.list(filter, pageable, principal.staffId()), saleMapper::toResponse));
    }

    /**
     * Get details of a specific sale by its ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SALE_READ')")
    public ResponseEntity<SaleDetailedResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(saleMapper.toDetailedResponse(saleService.get(id)));
    }

    /**
     * Create a new sale listing.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SALE_WRITE')")
    public ResponseEntity<SaleResponse> create(@Valid @RequestBody SaleCreateRequest request,
                                               @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saleMapper.toResponse(saleService.createListing(request, principal.staffId())));
    }

    /**
     * Sell the item for the given sale ID.
     */
    @PostMapping("/{id}/sell")
    @PreAuthorize("hasAuthority('SALE_WRITE')")
    public ResponseEntity<SaleResponse> sell(@PathVariable Long id,
                                             @Valid @RequestBody SaleSellRequest request,
                                             @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(saleMapper.toResponse(saleService.sell(id, request, principal.staffId())));
    }

    /**
     * Cancel the sale with the given ID.
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('SALE_CANCEL')")
    public ResponseEntity<SaleResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(saleMapper.toResponse(saleService.cancel(id)));
    }
}
