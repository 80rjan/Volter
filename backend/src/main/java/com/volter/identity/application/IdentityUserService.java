package com.volter.identity.application;

import com.volter.identity.domain.model.IdentityUser;
import com.volter.shop.shared.common.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdentityUserService {

    public IdentityUser getCurrentIdentityUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated())
            throw new UnauthorizedException("Not authenticated");

        return (IdentityUser) auth.getPrincipal();
    }
}
