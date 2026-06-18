package com.volter.shared.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public record StaffPrincipal(
        Long staffId,
        String username,
        Long shopId,
        String schemaName,
        TokenScope scope,
        Set<String> roles,
        Set<String> permissions
) implements UserDetails {

    public static StaffPrincipal preAuth(Long staffId, String username) {
        return new StaffPrincipal(staffId, username, null, null, TokenScope.PRE_AUTH, Set.of(), Set.of());
    }

    public static StaffPrincipal access(Long staffId,
                                        String username,
                                        Long shopId,
                                        String schemaName,
                                        Set<String> roles,
                                        Set<String> permissions) {
        return new StaffPrincipal(staffId, username, shopId, schemaName, TokenScope.ACCESS, roles, permissions);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (scope == TokenScope.PRE_AUTH) {
            return List.of(new SimpleGrantedAuthority("ROLE_PRE_AUTH"));
        }
        return Stream.concat(
                roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)),
                permissions.stream().map(SimpleGrantedAuthority::new)
        ).toList();
    }

    @Override public String getPassword() { return ""; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
