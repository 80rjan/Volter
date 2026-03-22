package com.volter.backend.sale.domain.repository;

import com.volter.backend.sale.domain.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByCustomer_Id(Long customerId);
}
