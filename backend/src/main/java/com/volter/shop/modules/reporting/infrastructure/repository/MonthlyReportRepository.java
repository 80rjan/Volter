package com.volter.shop.modules.reporting.infrastructure.repository;

import com.volter.shop.modules.reporting.domain.model.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long>,
        JpaSpecificationExecutor<MonthlyReport> {

    Optional<MonthlyReport> findByYearAndMonth(Integer year, Integer month);

    boolean existsByYearAndMonth(Integer year, Integer month);
}
