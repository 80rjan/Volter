package com.volter.backend.item.domain.repository;

import com.volter.backend.item.domain.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
