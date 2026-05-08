package com.volter.shop.modules.cashregister.infrastructure;

import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {

}
