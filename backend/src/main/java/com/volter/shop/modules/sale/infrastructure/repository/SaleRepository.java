package com.volter.shop.modules.sale.infrastructure.repository;

import com.volter.shop.modules.sale.domain.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> ,
        JpaSpecificationExecutor<Sale> {

    List<Sale> findByCustomer_Id(Long customerId);

    @Query("SELECT COUNT(s) FROM Sale s WHERE MONTH(s.createdAt) = :month AND YEAR(s.createdAt) = :year")
    Integer countByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);
}
