package com.volter.shop.modules.sale.domain.repository;

import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.domain.model.enums.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    /**
     * Total profit (sale price minus purchase price) on sales sold between
     * {@code from} and {@code to} (inclusive), by sale date.
     */
    @Query("""
            select coalesce(sum(s.salePrice.amount - s.purchasePrice.amount), 0L)
            from Sale s
            where s.status = :sold
              and s.soldAt is not null
              and cast(s.soldAt as date) between :from and :to
            """)
    long profitBetween(@Param("from") LocalDate from,
                       @Param("to") LocalDate to,
                       @Param("sold") SaleStatus sold);

    default long profitBetween(LocalDate from, LocalDate to) {
        return profitBetween(from, to, SaleStatus.SOLD);
    }
}
