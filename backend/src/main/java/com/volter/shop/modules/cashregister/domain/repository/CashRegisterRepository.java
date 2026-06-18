package com.volter.shop.modules.cashregister.domain.repository;

import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {

    Optional<CashRegister> findByCode(String code);

    boolean existsByCode(String code);
}
