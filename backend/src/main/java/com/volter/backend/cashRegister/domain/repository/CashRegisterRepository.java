package com.volter.backend.cashRegister.domain.repository;

import com.volter.backend.cashRegister.domain.model.CashRegister;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {

}
