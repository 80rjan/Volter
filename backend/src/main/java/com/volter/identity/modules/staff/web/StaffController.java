package com.volter.identity.modules.staff.web;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.identity.modules.staff.application.dto.*;
import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.identity.modules.staff.domain.model.StaffRole;
import com.volter.identity.modules.staff.infrastructure.mapper.StaffMapper;
import com.volter.identity.modules.staff.infrastructure.mapper.StaffRoleMapper;
import com.volter.platform.modules.shop.application.ShopService;
import com.volter.platform.modules.shop.application.dto.ShopResponse;
import com.volter.platform.modules.shop.domain.model.Shop;
import com.volter.platform.modules.shop.infrastructure.mapper.ShopMapper;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for managing staff members.
 * The caller has access to information and can perform actions for staff members that they manage.
 */
@RestController
@RequestMapping("/admin/staff")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('STAFF_MANAGE')")
public class StaffController {

    private final StaffService staffService;
    private final StaffMapper staffMapper;
    private final StaffRoleMapper staffRoleMapper;
    private final ShopService shopService;
    private final ShopMapper shopMapper;

    /**
     * List all staff members, paginated and with optional filters.
     */
    @GetMapping
    public ResponseEntity<PageResponse<StaffResponse>> list(@ModelAttribute StaffFilterRequest filter,
                                                            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(staffService.list(filter, pageable), staffMapper::toResponse));
    }

    /**
     * Get a single staff member by ID, if the caller has access to it.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StaffDetailedResponse> get(@PathVariable Long id,
                                                     @AuthenticationPrincipal StaffPrincipal principal) {
        Staff staff = staffService.get(id, principal.staffId());
        return ResponseEntity.ok(staffMapper.toDetailedResponse(staff, shopsForStaff(staff)));
    }

    /**
     * Create a new staff member.
     */
    @PostMapping
    public ResponseEntity<StaffDetailedResponse> create(@Valid @RequestBody StaffCreateRequest request) {
        Staff staff = staffService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(staffMapper.toDetailedResponse(staff, shopsForStaff(staff)));
    }

    /**
     * Update an existing staff member. Only non-null fields in the request will be updated.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<StaffDetailedResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody StaffUpdateRequest request,
                                                        @AuthenticationPrincipal StaffPrincipal principal) {
        Staff staff = staffService.update(id, request, principal.staffId());
        return ResponseEntity.ok(staffMapper.toDetailedResponse(staff, shopsForStaff(staff)));
    }

    /**
     * Activate a staff member.
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        staffService.activate(id, principal.staffId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivate a staff member.
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        staffService.deactivate(id, principal.staffId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Suspend a staff member.
     */
    @PostMapping("/{id}/suspend")
    public ResponseEntity<Void> suspend(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        staffService.suspend(id, principal.staffId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Soft delete a staff member.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        staffService.softDelete(id, principal.staffId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Revoke a role grant from a staff member. The caller must have access to the staff member.
     */
    @DeleteMapping("/{id}/roles/{staffRoleId}")
    public ResponseEntity<Void> revokeRoleGrant(@PathVariable Long id,
                                                @PathVariable Long staffRoleId,
                                                @AuthenticationPrincipal StaffPrincipal principal) {
        staffService.revokeRoleGrant(id, staffRoleId, principal.staffId());
        return ResponseEntity.noContent().build();
    }

    // ----- response enrichment -----
    // StaffRole references its owning shop by id only (Shop lives in the platform context),
    // so resolve the shops here and hand them to the mapper(s) as context.

    private Map<Long, ShopResponse> shopsForStaff(Staff staff) {
        return shopsFor(staff.getStaffRoles().stream().map(StaffRole::getShopId).collect(Collectors.toSet()));
    }

    private Map<Long, ShopResponse> shopsFor(Collection<Long> shopIds) {
        return shopService.findByIds(shopIds).stream()
                .collect(Collectors.toMap(Shop::getId, shopMapper::toResponse));
    }
}
