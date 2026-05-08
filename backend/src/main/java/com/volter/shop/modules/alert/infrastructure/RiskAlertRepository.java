package com.volter.shop.modules.alert.infrastructure;

import com.volter.shop.modules.alert.domain.RiskAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskAlertRepository extends JpaRepository<RiskAlert, Long> {
}
