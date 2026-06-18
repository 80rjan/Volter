package com.volter.identity.modules.permission.application;

import com.volter.identity.modules.permission.application.dto.PermissionFilterRequest;
import com.volter.identity.modules.permission.domain.model.Permission;
import com.volter.identity.modules.permission.domain.repository.PermissionRepository;
import com.volter.identity.modules.permission.domain.specification.PermissionSpecification;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository permissionRepository;

    /**
     * Lists permissions based on the provided filter and pagination parameters.
     */
    public Page<Permission> list(PermissionFilterRequest filter, Pageable pageable) {
        return permissionRepository.findAll(PermissionSpecification.matches(filter), pageable);
    }

    /**
     * Retrieves a specific permission by its ID.
     * Throws ResourceNotFoundException if the permission does not exist.
     */
    public Permission get(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + id));
    }
}
