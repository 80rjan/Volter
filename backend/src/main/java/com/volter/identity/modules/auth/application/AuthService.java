package com.volter.identity.modules.auth.application;

import com.volter.identity.modules.auth.application.dto.LoginRequest;
import com.volter.identity.modules.auth.application.dto.LoginResponse;
import com.volter.identity.modules.auth.application.dto.SelectShopRequest;
import com.volter.identity.modules.auth.application.dto.ShopOption;
import com.volter.identity.modules.auth.application.dto.TokenResponse;
import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.identity.modules.staff.domain.repository.StaffRepository;
import com.volter.platform.modules.authevent.domain.model.AuthEvent;
import com.volter.platform.modules.authevent.domain.model.enums.AuthEventType;
import com.volter.platform.modules.authevent.domain.repository.AuthEventRepository;
import com.volter.platform.modules.shop.domain.model.StaffShop;
import com.volter.platform.modules.shop.domain.model.enums.StaffShopStatus;
import com.volter.platform.modules.shop.domain.repository.StaffShopRepository;
import com.volter.shared.security.JwtService;
import com.volter.shared.web.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StaffRepository staffRepository;
    private final StaffShopRepository staffShopRepository;
    private final AuthEventRepository authEventRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Authenticates a staff member based on the provided login request, IP address, and user agent.
     * Find the staff member by username. If the staff member does not exist, throw a BadCredentialsException.
     * Check if staff member is active and password matches. If authentication fails, record a login failure event and throw a BadCredentialsException.
     * Fetch active shop assignments for the staff member.
     * Return a login response containing a pre-auth token and the list of assigned shops.
     */
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        Staff staff = staffRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!staff.isActive() || !passwordEncoder.matches(request.password(), staff.getPasswordHash())) {
            authEventRepository.save(AuthEvent.record(staff.getId(), AuthEventType.LOGIN_FAILURE, ipAddress, userAgent));
            throw new BadCredentialsException("Invalid credentials");
        }

        List<StaffShop> assignments = staffShopRepository.findAllByStaffIdAndStatus(staff.getId(), StaffShopStatus.ACTIVE);
        if (assignments.isEmpty()) {
            authEventRepository.save(AuthEvent.record(staff.getId(), AuthEventType.LOGIN_FAILURE, ipAddress, userAgent));
            throw new UnauthorizedException("No shops assigned to this staff");
        }

        authEventRepository.save(AuthEvent.record(staff.getId(), AuthEventType.LOGIN_SUCCESS, ipAddress, userAgent));

        return new LoginResponse(
                jwtService.issuePreAuthToken(staff.getId()),
                assignments.stream()
                        .map(ss -> new ShopOption(ss.getShop().getId(), ss.getShop().getName(), ss.getShop().getCode()))
                        .toList()
        );
    }

    /**
     * Check if the authenticated staff member is assigned to the requested shop and has an active status.
     * If the staff member is not assigned to the shop, throw an UnauthorizedException.
     * If the staff member is assigned, issue a new access token scoped to the selected shop and return it in a TokenResponse along with the shop ID.
     */
    @Transactional(readOnly = true)
    public TokenResponse selectShop(Long staffId, SelectShopRequest request) {
        boolean assigned = staffShopRepository.existsByStaffIdAndShop_IdAndStatus(
                staffId, request.shopId(), StaffShopStatus.ACTIVE);
        if (!assigned) {
            throw new UnauthorizedException("Staff is not assigned to this shop");
        }
        return new TokenResponse(jwtService.issueAccessToken(staffId, request.shopId()), request.shopId());
    }
}
