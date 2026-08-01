package com.volter.platform.modules.report.web;

import com.volter.platform.modules.report.application.ReportService;
import com.volter.platform.modules.report.application.dto.response.PeriodReportResponse;
import com.volter.platform.modules.report.application.dto.response.ReportDetailedResponse;
import com.volter.platform.modules.report.application.dto.response.StaffPerformanceResponse;
import com.volter.platform.modules.report.application.dto.request.ReportFilterRequest;
import com.volter.platform.modules.report.application.dto.response.ReportResponse;
import com.volter.platform.modules.report.domain.model.Report;
import com.volter.platform.modules.report.infrastructure.mapper.ReportMapper;
import com.volter.shared.security.StaffPrincipal;
import com.volter.shared.web.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

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
                reportService.list(filter, pageable, principal.staffId(), principal.shopId()), reportMapper::toResponse));
    }

    /**
     * Computes an on-the-fly summary for an arbitrary date range (same shape as a
     * monthly report) for the caller's shop. Nothing is persisted.
     */
    @GetMapping("/period")
    public ResponseEntity<PeriodReportResponse> period(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return ResponseEntity.ok(reportService.periodSummary(dateFrom, dateTo));
    }

    /**
     * Generates and persists the shop-wide monthly summary for the given year/month
     * (caller's shop), e.g. to backfill a month the scheduled job missed or to recompute
     * one after a formula change. Does NOT remove an existing report for that month —
     * delete any stale row first to avoid a duplicate.
     */
    @PostMapping("/monthly")
    public ResponseEntity<ReportResponse> generateMonthly(
            @RequestParam int year, @RequestParam int month,
            @AuthenticationPrincipal StaffPrincipal principal) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("month must be between 1 and 12");
        }
        YearMonth ym = YearMonth.of(year, month);
        Report report = reportService.generateMonthlySummary(principal.shopId(), ym.atDay(1), ym.atEndOfMonth());
        return ResponseEntity.ok(reportMapper.toResponse(report));
    }

    /**
     * Computes a staff member's performance for a date range on the fly (not persisted),
     * for the caller's shop. Visible to the staff themselves and to their managers.
     */
    @GetMapping("/staff/{staffId}/period")
    public ResponseEntity<StaffPerformanceResponse> staffPeriod(
            @PathVariable Long staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(reportService.staffPerformancePeriod(staffId, dateFrom, dateTo, principal.staffId()));
    }

    /**
     * Retrieves a specific report by its ID.
     * Only system reports and reports owned by the caller can be accessed.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReportDetailedResponse> get(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(reportMapper.toDetailedResponse(reportService.get(id, principal.staffId(), principal.shopId())));
    }
}
