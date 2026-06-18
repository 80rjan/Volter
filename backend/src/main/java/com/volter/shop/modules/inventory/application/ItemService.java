package com.volter.shop.modules.inventory.application;

import com.volter.shop.modules.inventory.application.dto.ItemCreateRequest;
import com.volter.shop.modules.inventory.application.dto.ItemFilterRequest;
import com.volter.shop.modules.inventory.application.dto.ItemStatusChangeRequest;
import com.volter.shop.modules.inventory.application.dto.ItemUpdateRequest;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.ItemStatusHistory;
import com.volter.shop.modules.inventory.domain.repository.ItemRepository;
import com.volter.shop.modules.inventory.domain.repository.ItemStatusHistoryRepository;
import com.volter.shop.modules.inventory.domain.specification.ItemSpecification;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemStatusHistoryRepository historyRepository;

    /**
     * List all items by optional filters.
     */
    @Transactional(readOnly = true)
    public Page<Item> list(ItemFilterRequest filter, Pageable pageable) {
        return itemRepository.findAll(ItemSpecification.matches(filter), pageable);
    }

    /**
     * Get one item by its ID.
     */
    @Transactional(readOnly = true)
    public Item get(Long id) {
        return loadItem(id);
    }

    /**
     * Create an item.
     */
    public Item create(ItemCreateRequest request) {
        Item item = Item.builder()
                .type(request.type())
                .origin(request.origin())
                .status(request.initialStatus())
                .description(request.description())
                .attributes(request.attributes() == null ? new HashMap<>() : new HashMap<>(request.attributes()))
                .build();
        return itemRepository.save(item);
    }

    /**
     * Update item info.
     */
    public Item update(Long id, ItemUpdateRequest request) {
        Item item = loadItem(id);
        if (request.description() != null) {
            item.describe(request.description());
        }
        if (request.attributes() != null) {
            request.attributes().forEach(item::putAttribute);
        }
        return item;
    }

    /**
     * Change item active status.
     * Create item status history audit.
     */
    public ItemStatusHistory changeStatus(Long id, ItemStatusChangeRequest request, Long staffId) {
        Item item = loadItem(id);
        ItemStatusHistory history = item.changeStatus(request.newStatus(), staffId);
        historyRepository.save(history);
        return history;
    }

    /**
     * Get status history for an item by its ID.
     */
    @Transactional(readOnly = true)
    public List<ItemStatusHistory> history(Long id) {
        loadItem(id);
        return historyRepository.findAllByItem_IdOrderByOccurredAtDesc(id);
    }

    // ----- status transitions driven by other modules (pawn / sale) -----
    // Other modules hold a managed Item (e.g. from a contract) and ask inventory
    // to transition it; the status-history write stays inside this module.

    public void markInPawn(Item item, Long staffId) {
        historyRepository.save(item.markInPawn(staffId));
    }

    public void markRedeemed(Item item, Long staffId) {
        historyRepository.save(item.markRedeemed(staffId));
    }

    public void markInSale(Item item, Long staffId) {
        historyRepository.save(item.markInSale(staffId));
    }

    public void markSold(Item item, Long staffId) {
        historyRepository.save(item.markSold(staffId));
    }

    public Item findOrFail(Long id) {
        return loadItem(id);
    }

    private Item loadItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
    }
}
