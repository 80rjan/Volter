package com.volter.shop.modules.sale.domain.repository;

import com.volter.shop.modules.sale.domain.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> ,
        JpaSpecificationExecutor<Sale> {

    List<Sale> findByCustomer_Id(Long customerId);
}
