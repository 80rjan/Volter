package com.volter.shop.modules.inventory.domain.repository;

import com.volter.shop.modules.inventory.domain.model.ItemStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemStatusHistoryRepository extends JpaRepository<ItemStatusHistory, Long> {

    List<ItemStatusHistory> findAllByItem_IdOrderByOccurredAtDesc(Long itemId);
}
