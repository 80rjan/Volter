package com.volter.shop.modules.inventory.application;

import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.infrastructure.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public Item getById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Transactional
    public Item getReferenceById(Long id) {
        return itemRepository.getReferenceById(id);
    }
}
