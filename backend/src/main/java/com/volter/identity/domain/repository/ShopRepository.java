package com.volter.identity.domain.repository;

import com.volter.identity.domain.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
