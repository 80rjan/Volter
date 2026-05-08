package com.volter.shop.modules.staff.infrastructure.repository;


import com.volter.shop.modules.staff.domain.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    boolean existsById(Long id);
    boolean existsByIdentityUserId(Long identityUserId);
}
