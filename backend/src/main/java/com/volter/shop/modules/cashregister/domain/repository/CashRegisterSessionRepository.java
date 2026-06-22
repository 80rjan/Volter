package com.volter.shop.modules.cashregister.domain.repository;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CashRegisterSessionRepository extends JpaRepository<CashRegisterSession, Long>, JpaSpecificationExecutor<CashRegisterSession> {

    // open-in-view is disabled; callers (e.g. close-session) map the returned
    // session to a response that reads the parent register's code, so pre-fetch it.
    @EntityGraph(attributePaths = "cashRegister")
    Optional<CashRegisterSession> findByCashRegister_IdAndStatus(Long cashRegisterId, CashRegisterSessionStatus status);

    // The most recently closed session for a register, to compare its counted
    // closing balance against a new session's opening balance.
    Optional<CashRegisterSession> findFirstByCashRegister_IdAndStatusOrderByClosedAtDesc(Long cashRegisterId, CashRegisterSessionStatus status);

    // open-in-view is disabled; the session response (mapped in the controller)
    // needs the parent register's code, so pre-fetch the register.

    @Override
    @EntityGraph(attributePaths = "cashRegister")
    Optional<CashRegisterSession> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "cashRegister")
    Page<CashRegisterSession> findAll(Specification<CashRegisterSession> spec, Pageable pageable);
}
