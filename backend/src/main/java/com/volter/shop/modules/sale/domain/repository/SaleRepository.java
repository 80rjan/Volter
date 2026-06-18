package com.volter.shop.modules.sale.domain.repository;

import com.volter.shop.modules.sale.domain.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long>, JpaSpecificationExecutor<Sale> {

    // open-in-view is disabled; the response (mapped in the controller) needs the
    // customer name, so pre-fetch the customer to avoid a lazy load after the
    // transaction closes.

    @Override
    @EntityGraph(attributePaths = "customer")
    Optional<Sale> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "customer")
    Page<Sale> findAll(Specification<Sale> spec, Pageable pageable);
}
