package com.volter.identity.modules.staff.domain.repository;

import com.volter.identity.modules.staff.domain.model.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRoleRepository extends JpaRepository<StaffRole, Long> {
}
