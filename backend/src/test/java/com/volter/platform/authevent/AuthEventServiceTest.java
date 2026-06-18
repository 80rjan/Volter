package com.volter.platform.authevent;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.platform.modules.authevent.application.AuthEventService;
import com.volter.platform.modules.authevent.application.dto.AuthEventFilterRequest;
import com.volter.platform.modules.authevent.domain.repository.AuthEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthEventServiceTest {

    @Mock
    private AuthEventRepository authEventRepository;
    @Mock
    private StaffService staffService;

    @InjectMocks
    private AuthEventService authEventService;

    @Test
    @DisplayName("list scopes to the caller's team and forwards to the repository")
    void list_scopesToTeam() {
        Pageable pageable = PageRequest.of(0, 20);
        when(staffService.findSubordinateStaffIds(3L)).thenReturn(List.of(4L, 5L));
        when(authEventRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));

        authEventService.list(new AuthEventFilterRequest(null, null, null, null), pageable, 3L);

        verify(staffService).findSubordinateStaffIds(3L);
        verify(authEventRepository).findAll(any(Specification.class), eq(pageable));
    }
}
