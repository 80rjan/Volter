package com.volter.shop.modules.staff.application;

import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.staff.domain.repository.StaffRepository;
import com.volter.shop.shared.common.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffService {

    protected final StaffRepository staffRepository;

    public Staff getCurrentStaff() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }

        Long id = Long.parseLong(auth.getName());

        return getById(id);
    }

    public Staff getById(Long id) {
        return staffRepository.findById(id).orElseThrow(() -> new RuntimeException("Staff with ID " + id + " not found!"));
    }

}
