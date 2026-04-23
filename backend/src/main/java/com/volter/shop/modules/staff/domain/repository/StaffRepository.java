package com.volter.shop.modules.staff.domain.repository;


import com.volter.shop.modules.staff.domain.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    boolean existsById(Long id);
    boolean existsByUsername(String username);
}
