package com.volter.identity.modules.role.application;

import com.volter.identity.modules.permission.domain.model.Permission;
import com.volter.identity.modules.permission.domain.repository.PermissionRepository;
import com.volter.identity.modules.role.application.dto.RoleCreateRequest;
import com.volter.identity.modules.role.application.dto.RoleFilterRequest;
import com.volter.identity.modules.role.application.dto.RolePermissionGrantRequest;
import com.volter.identity.modules.role.application.dto.RoleUpdateRequest;
import com.volter.identity.modules.role.domain.model.Role;
import com.volter.identity.modules.role.domain.repository.RoleRepository;
import com.volter.identity.modules.role.domain.specification.RoleSpecification;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    /**
     * List all roles matching the filter criteria, paginated.
     */
    @Transactional(readOnly = true)
    public Page<Role> list(RoleFilterRequest filter, Pageable pageable) {
        return roleRepository.findAll(RoleSpecification.matches(filter), pageable);
    }

    /**
     * Get a single role by its id, or throw if not found.
     */
    @Transactional(readOnly = true)
    public Role get(Long id) {
        return loadRole(id);
    }

    /**
     * Create a new role.
     * The name must be unique.
     */
    public Role create(RoleCreateRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new BusinessRuleException("Role name already exists: " + request.name());
        }
        Set<Permission> permissions = loadPermissions(request.permissionIds());
        Role role = Role.builder()
                .name(request.name())
                .permissions(new HashSet<>(permissions))
                .build();
        return roleRepository.save(role);
    }

    /**
     * Rename an existing role.
     * The new name must be unique among all roles except the one being renamed.
     */
    public Role rename(Long id, RoleUpdateRequest request) {
        Role role = loadRole(id);
        if (roleRepository.existsByName(request.name())) {
            throw new BusinessRuleException("Role name already exists: " + request.name());
        }
        role.rename(request.name());
        return role;
    }

    /**
     * Grant one or more permissions to a role.
     * Permissions that are already granted will be ignored (because they're kept as a set).
     */
    public Role grantPermissions(Long roleId, RolePermissionGrantRequest request) {
        Role role = loadRole(roleId);
        loadPermissions(request.permissionIds()).forEach(role::grant);
        return role;
    }

    /**
     * Revoke a permission from a role.
     * If the role doesn't have the permission, this will be a no-op.
     */
    public Role revokePermission(Long roleId, Long permissionId) {
        Role role = loadRole(roleId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + permissionId));
        role.revoke(permission);
        return role;
    }

    // ----- INTERNAL HELPER METHODS -----

    private Role loadRole(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
    }

    private Set<Permission> loadPermissions(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Set.of();
        List<Permission> found = permissionRepository.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new ResourceNotFoundException("One or more permissions not found");
        }
        return new HashSet<>(found);
    }
}
