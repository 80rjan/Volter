package com.volter.shop.modules.cashregister.domain.repository;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSessionDiscrepancy;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CashRegisterSessionDiscrepancyRepository extends JpaRepository<CashRegisterSessionDiscrepancy, Long>, JpaSpecificationExecutor<CashRegisterSessionDiscrepancy> {

    // resolveDiscrepancy reads the discrepancy's session (its operator's staffId);
    // fetch it in the same query instead of triggering a lazy second select.
    @Override
    @EntityGraph(attributePaths = "session")
    Optional<CashRegisterSessionDiscrepancy> findById(Long id);
}
