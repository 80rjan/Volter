//package com.volter.backend.shared.common.utils;
//
//import com.volter.backend.modules.cashregister.application.CashRegisterService;
//import com.volter.backend.shared.common.exceptions.UnauthorizedException;
//import com.volter.backend.modules.staff.application.StaffService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class Validate {
//
//    private final StaffService staffService;
//    private final CashRegisterService cashRegisterService;
//
//    public void validateStaffIsAuthenticated(Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new UnauthorizedException("Staff is not authenticated");
//        }
//    }
//
//    public Long extractStaffId(Authentication authentication) {
//        validateStaffIsAuthenticated(authentication);
//
//        // The JwtRequestFilter stores staffId as the principal
//        Object principal = authentication.getPrincipal();
//
//        if (principal instanceof Long) {
//            return (Long) principal;
//        }
//
//        throw new UnauthorizedException("Invalid authentication principal");
//    }
//}