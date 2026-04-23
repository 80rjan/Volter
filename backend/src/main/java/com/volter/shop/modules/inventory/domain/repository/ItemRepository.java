package com.volter.shop.modules.inventory.domain.repository;

import com.volter.shop.modules.inventory.domain.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
