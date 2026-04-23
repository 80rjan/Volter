package com.volter.shared.multitenancy;

import com.volter.identity.domain.model.IdentityUser;
import com.volter.identity.domain.model.enums.RoleEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@NoArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated()
                    && auth.getPrincipal() instanceof IdentityUser user) {

                if (user.getRole().getName() == RoleEnum.ADMIN) {
                    TenantContext.setCurrentTenant(TenantContext.GLOBAL_SCHEMA);
                } else {
                    String shopSchema = user.getCurrentShopSchema();

                    if (shopSchema == null || shopSchema.isBlank()) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN,
                                "No shop associated with this token");
                        return;
                    }

                    TenantContext.setCurrentTenant(shopSchema);
                }
            }

            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

}
