package com.volter.platform.modules.authevent.domain.repository;

import com.volter.platform.modules.authevent.domain.model.AuthEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuthEventRepository extends JpaRepository<AuthEvent, Long>, JpaSpecificationExecutor<AuthEvent> {
}
