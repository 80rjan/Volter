package com.volter.backend.util;

import com.volter.backend.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Validate {

    public void validateStaffIsAuthenticated(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Staff is not authenticated");
        }
    }

    public Long extractStaffId(Authentication authentication) {
        validateStaffIsAuthenticated(authentication);

        // The JwtRequestFilter stores staffId as the principal
        Object principal = authentication.getPrincipal();

        if (principal instanceof Long) {
            return (Long) principal;
        }

        throw new UnauthorizedException("Invalid authentication principal");
    }
}