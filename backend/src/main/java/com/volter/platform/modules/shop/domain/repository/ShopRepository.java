package com.volter.platform.modules.shop.domain.repository;

import com.volter.platform.modules.shop.domain.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long>, JpaSpecificationExecutor<Shop> {

    Optional<Shop> findByCode(String code);

    Optional<Shop> findBySchemaName(String schemaName);

    boolean existsByCode(String code);

    boolean existsBySchemaName(String schemaName);
}
