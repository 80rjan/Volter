package com.volter.shop.modules.pawn.domain.repository;

import com.volter.shop.modules.pawn.domain.model.PawnContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PawnContractRepository extends JpaRepository<PawnContract, Long>, JpaSpecificationExecutor<PawnContract> {

    // open-in-view is disabled, so responses are mapped in the controller after the
    // service transaction closes. Pre-fetch the associations the responses read so
    // mapping does not trigger a lazy load on a detached entity:
    //  - customer + item: both list and detailed responses
    //  - extensions: only the detailed response (a lazy @OneToMany)

    @Override
    @EntityGraph(attributePaths = {"customer", "item", "extensions"})
    Optional<PawnContract> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"customer", "item"})
    Page<PawnContract> findAll(Specification<PawnContract> spec, Pageable pageable);

    // For the totals bar: aggregate over the whole filtered set. Eager-load the
    // item so summing gold weight (in item.attributes) doesn't trigger N+1.
    @Override
    @EntityGraph(attributePaths = {"item"})
    List<PawnContract> findAll(Specification<PawnContract> spec);
}
