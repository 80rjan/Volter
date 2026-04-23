package com.volter.identity.domain.repository;

import com.volter.identity.domain.model.IdentityUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdentityUserRepository extends JpaRepository<IdentityUser, Long> {

    Optional<IdentityUser> findByUsername(String username);
}
