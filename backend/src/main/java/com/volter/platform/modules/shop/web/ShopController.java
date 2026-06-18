package com.volter.platform.modules.shop.web;

import com.volter.platform.modules.shop.application.ShopService;
import com.volter.platform.modules.shop.application.dto.*;
import com.volter.platform.modules.shop.infrastructure.mapper.ShopMapper;
import com.volter.platform.modules.shop.infrastructure.mapper.StaffShopMapper;
import com.volter.shared.web.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/shops")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SHOP_MANAGE')")
public class ShopController {

    private final ShopService shopService;
    private final ShopMapper shopMapper;
    private final StaffShopMapper staffShopMapper;

    /**
     * Lists paginated shops based on the provided filter.
     */
    @GetMapping
    public ResponseEntity<PageResponse<ShopResponse>> list(@ModelAttribute ShopFilterRequest filter, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(shopService.list(filter, pageable), shopMapper::toResponse));
    }

    /**
     * Retrieves a specific shop by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShopResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(shopMapper.toResponse(shopService.get(id)));
    }

    /**
     * Creates a new shop based on the provided request data.
     */
    @PostMapping
    public ResponseEntity<ShopResponse> create(@Valid @RequestBody ShopCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shopMapper.toResponse(shopService.create(request)));
    }

    /**
     * Updates an existing shop identified by its ID.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ShopResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody ShopUpdateRequest request) {
        return ResponseEntity.ok(shopMapper.toResponse(shopService.updateContactDetails(id, request)));
    }

    /**
     * Closes the shop identified by its ID, making it inactive.
     */
    @PostMapping("/{id}/close")
    public ResponseEntity<Void> close(@PathVariable Long id) {
        shopService.close(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reopens the shop identified by its ID, making it active again.
     */
    @PostMapping("/{id}/reopen")
    public ResponseEntity<Void> reopen(@PathVariable Long id) {
        shopService.reopen(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lists staff assignments for the shop identified by its ID.
     */
    @GetMapping("/{id}/staff")
    public ResponseEntity<List<StaffShopResponse>> listAssignments(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.listAssignments(id).stream().map(staffShopMapper::toResponse).toList());
    }

    /**
     * Assigns a staff member to the shop identified by its ID based on the provided request data.
     */
    @PostMapping("/{id}/staff")
    public ResponseEntity<StaffShopResponse> assignStaff(@PathVariable Long id,
                                                         @Valid @RequestBody StaffShopAssignRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffShopMapper.toResponse(shopService.assignStaff(id, request)));
    }

    /**
     * Unassigns a staff member from the shop identified by its ID based on the assignment ID.
     */
    @DeleteMapping("/{id}/staff/{assignmentId}")
    public ResponseEntity<Void> unassignStaff(@PathVariable Long id, @PathVariable Long assignmentId) {
        shopService.unassignStaff(id, assignmentId);
        return ResponseEntity.noContent().build();
    }
}
