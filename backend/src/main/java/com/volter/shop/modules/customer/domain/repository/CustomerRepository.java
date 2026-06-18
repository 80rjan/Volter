package com.volter.shop.modules.customer.domain.repository;

import com.volter.shop.modules.customer.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByNationalId(String nationalId);

    boolean existsByNationalId(String nationalId);
}
