package com.volter.shared.multitenancy;

import com.volter.shared.security.StaffPrincipal;
import com.volter.shared.security.TokenScope;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null
                    && auth.isAuthenticated()
                    && auth.getPrincipal() instanceof StaffPrincipal principal
                    && principal.scope() == TokenScope.ACCESS
                    && principal.schemaName() != null) {
                TenantContext.setCurrentTenant(principal.schemaName());
            } else {
                TenantContext.setCurrentTenant(TenantContext.GLOBAL_SCHEMA);
            }
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
