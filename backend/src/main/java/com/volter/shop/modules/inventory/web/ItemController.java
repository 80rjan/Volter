package com.volter.shop.modules.inventory.web;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.application.dto.*;
import com.volter.shop.modules.inventory.domain.model.ItemStatusHistory;
import com.volter.shop.modules.inventory.infrastructure.mapper.ItemMapper;
import com.volter.shared.web.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for managing inventory items.
 * Item of each status can be returned, depending on the status filter (no restrictions).
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final StaffService staffService;

    /**
     * List all items by optional filters.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ITEM_READ')")
    public ResponseEntity<PageResponse<ItemResponse>> list(@ModelAttribute ItemFilterRequest filter, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(itemService.list(filter, pageable), itemMapper::toResponse));
    }

    /**
     * List one item by its ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ITEM_READ')")
    public ResponseEntity<ItemResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(itemMapper.toResponse(itemService.get(id)));
    }

    /**
     * Create a new item.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ITEM_WRITE')")
    public ResponseEntity<ItemResponse> create(@Valid @RequestBody ItemCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemMapper.toResponse(itemService.create(request)));
    }

    /**
     * Update information about an item.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ITEM_WRITE')")
    public ResponseEntity<ItemResponse> update(@PathVariable Long id, @Valid @RequestBody ItemUpdateRequest request) {
        return ResponseEntity.ok(itemMapper.toResponse(itemService.update(id, request)));
    }

    /**
     * Get history of status changes for an item.
     */
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAuthority('ITEM_READ')")
    public ResponseEntity<List<ItemStatusHistoryResponse>> history(@PathVariable Long id) {
        List<ItemStatusHistory> history = itemService.history(id);
        Map<Long, String> staffNames = staffService.findStaffNames(
                history.stream().map(ItemStatusHistory::getChangedByStaffId).collect(Collectors.toSet()));
        return ResponseEntity.ok(history.stream()
                .map(h -> itemMapper.toResponse(h, staffNames.get(h.getChangedByStaffId())))
                .toList());
    }
}
