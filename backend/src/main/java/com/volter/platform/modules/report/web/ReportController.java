package com.volter.platform.modules.report.web;

import com.volter.platform.modules.report.application.ReportService;
import com.volter.platform.modules.report.application.dto.response.ReportDetailedResponse;
import com.volter.platform.modules.report.application.dto.request.ReportFilterRequest;
import com.volter.platform.modules.report.application.dto.response.ReportResponse;
import com.volter.platform.modules.report.infrastructure.mapper.ReportMapper;
import com.volter.shared.security.StaffPrincipal;
import com.volter.shared.web.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('REPORT_READ')")
public class ReportController {

    private final ReportService reportService;
    private final ReportMapper reportMapper;

    /**
     * Lists paginated reports based on the provided filter.
     * Only system reports and reports owned by the caller are returned.
     */
    @GetMapping
    public ResponseEntity<PageResponse<ReportResponse>> list(@ModelAttribute ReportFilterRequest filter, Pageable pageable,
                                                             @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(PageResponse.of(
                reportService.list(filter, pageable, principal.staffId()), reportMapper::toResponse));
    }

    /**
     * Retrieves a specific report by its ID.
     * Only system reports and reports owned by the caller can be accessed.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReportDetailedResponse> get(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(reportMapper.toDetailedResponse(reportService.get(id, principal.staffId())));
    }
}
