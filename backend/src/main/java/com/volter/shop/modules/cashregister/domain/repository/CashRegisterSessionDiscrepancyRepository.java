package com.volter.shop.modules.cashregister.domain.repository;

import com.volter.shop.modules.cashregister.application.dto.StaffDiscrepancySummary;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSessionDiscrepancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface CashRegisterSessionDiscrepancyRepository extends JpaRepository<CashRegisterSessionDiscrepancy, Long>, JpaSpecificationExecutor<CashRegisterSessionDiscrepancy> {

    /** Discrepancy count + signed total for a staff member's sessions in a range, for the staff performance report. */
    @Query("""
            select new com.volter.shop.modules.cashregister.application.dto.StaffDiscrepancySummary(
                count(d), coalesce(sum(d.difference), 0L))
            from CashRegisterSessionDiscrepancy d
            where d.session.staffId = :staffId
              and cast(d.createdAt as date) between :from and :to
            """)
    StaffDiscrepancySummary summarizeForStaff(@Param("staffId") Long staffId,
                                              @Param("from") LocalDate from,
                                              @Param("to") LocalDate to);

    // resolveDiscrepancy reads the discrepancy's session (its operator's staffId);
    // fetch it in the same query instead of triggering a lazy second select.
    @Override
    @EntityGraph(attributePaths = "session")
    Optional<CashRegisterSessionDiscrepancy> findById(Long id);

    // open-in-view is disabled; the list response maps session id + operator
    // staffId, so pre-fetch the session to avoid a LazyInitializationException.
    @Override
    @EntityGraph(attributePaths = "session")
    Page<CashRegisterSessionDiscrepancy> findAll(Specification<CashRegisterSessionDiscrepancy> spec, Pageable pageable);
}
