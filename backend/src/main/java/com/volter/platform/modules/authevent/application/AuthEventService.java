package com.volter.platform.modules.authevent.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.platform.modules.authevent.application.dto.AuthEventFilterRequest;
import com.volter.platform.modules.authevent.domain.model.AuthEvent;
import com.volter.platform.modules.authevent.domain.repository.AuthEventRepository;
import com.volter.platform.modules.authevent.domain.specification.AuthEventSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthEventService {

    private final AuthEventRepository authEventRepository;
    private final StaffService staffService;

    /**
     * Auth events the caller may see: their own plus those of every staff member
     * below them in the management tree (recursive), narrowed by the filter.
     */
    public Page<AuthEvent> list(AuthEventFilterRequest filter, Pageable pageable, Long staffId) {
        List<Long> visibleStaffIds = new ArrayList<>(staffService.findSubordinateStaffIds(staffId));
        visibleStaffIds.add(staffId);
        return authEventRepository.findAll(
                AuthEventSpecification.matches(filter).and(AuthEventSpecification.staffIdIn(visibleStaffIds)),
                pageable);
    }
}
