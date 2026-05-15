// identity/application/AuthService.java
package com.volter.identity.application;

import com.volter.identity.web.request.LoginRequest;
import com.volter.identity.web.request.SwitchShopRequest;
import com.volter.identity.application.dto.TokenDTO;
import com.volter.identity.domain.model.IdentityUser;
import com.volter.identity.domain.model.Shop;
import com.volter.identity.domain.model.enums.RoleEnum;
import com.volter.identity.domain.repository.IdentityUserRepository;
import com.volter.identity.domain.repository.ShopRepository;
import com.volter.identity.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IdentityUserRepository userRepository;
    private final ShopRepository shopRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public TokenDTO login(LoginRequest request) {
        IdentityUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash()))
            throw new BadCredentialsException("Invalid credentials");

        if (user.isDeleted())
            throw new DisabledException("Account is disabled");

        if (user.getRole().getName() == RoleEnum.ADMIN) {
            String token = jwtUtil.generateToken(user.getId(), null);
            return new TokenDTO(token, RoleEnum.ADMIN.name(), null, null);
        }

        if (request.shopId() == null)
            throw new IllegalArgumentException("shopId is required");

        Shop shop = shopRepository.findById(request.shopId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        boolean hasAccess = user.getAssignedShops().stream()
                .anyMatch(s -> s.getId().equals(request.shopId()));

        if (!hasAccess)
            throw new AccessDeniedException("No access to shop: " + shop.getName());

        String token = jwtUtil.generateToken(user.getId(), shop.getSchemaName());

        return new TokenDTO(
                token,
                user.getRole().getName().name(),
                shop.getSchemaName(),
                shop.getName()
        );
    }

    public TokenDTO switchShop(SwitchShopRequest request, IdentityUser currentUser) {
        if (currentUser.getRole().getName() == RoleEnum.ADMIN)
            throw new IllegalStateException("ADMIN does not need to switch shops");

        Shop shop = shopRepository.findById(request.shopId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        boolean hasAccess = currentUser.getAssignedShops().stream()
                .anyMatch(s -> s.getId().equals(request.shopId()));

        if (!hasAccess)
            throw new AccessDeniedException("No access to shop: " + shop.getName());

        String token = jwtUtil.generateToken(currentUser.getId(), shop.getSchemaName());

        return new TokenDTO(
                token,
                currentUser.getRole().getName().name(),
                shop.getSchemaName(),
                shop.getName()
        );
    }
}
