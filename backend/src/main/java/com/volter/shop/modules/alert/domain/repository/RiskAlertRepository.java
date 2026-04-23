package com.volter.shop.modules.alert.domain.repository;

import com.volter.shop.modules.alert.domain.model.RiskAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskAlertRepository extends JpaRepository<RiskAlert, Long> {
}
