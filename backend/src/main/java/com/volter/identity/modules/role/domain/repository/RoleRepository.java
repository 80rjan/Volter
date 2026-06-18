package com.volter.identity.modules.role.domain.repository;

import com.volter.identity.modules.role.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}
