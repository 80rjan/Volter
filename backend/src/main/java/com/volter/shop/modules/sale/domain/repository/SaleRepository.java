package com.volter.shop.modules.sale.domain.repository;

import com.volter.shop.modules.sale.domain.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long>, JpaSpecificationExecutor<Sale> {

    // open-in-view is disabled; responses are mapped in the controller after the
    // transaction closes, and they read both the customer and the item, so
    // pre-fetch both to avoid a lazy load on a detached entity.

    @Override
    @EntityGraph(attributePaths = {"customer", "item"})
    Optional<Sale> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"customer", "item"})
    Page<Sale> findAll(Specification<Sale> spec, Pageable pageable);

    // For the totals bar: aggregate over the whole filtered set. Eager-load the
    // item so summing gold weight (in item.attributes) doesn't trigger N+1.
    @Override
    @EntityGraph(attributePaths = {"item"})
    List<Sale> findAll(Specification<Sale> spec);
}
