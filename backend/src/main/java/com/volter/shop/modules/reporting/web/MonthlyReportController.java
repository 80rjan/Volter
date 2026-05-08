package com.volter.shop.modules.reporting.web;

import com.volter.shop.modules.reporting.application.MonthlyReportService;
import com.volter.shop.modules.reporting.infrastructure.mapper.MonthlyReportMapper;
import com.volter.shop.modules.reporting.web.request.MonthlyReportFilterRequest;
import com.volter.shop.modules.reporting.web.response.MonthlyReportDetailedResponse;
import com.volter.shop.modules.reporting.web.response.MonthlyReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.base.path}/reports/monthly")
public class MonthlyReportController {

    private final MonthlyReportMapper mapper;
    private final MonthlyReportService service;

    @GetMapping
    public ResponseEntity<Page<MonthlyReportResponse>> getAll(MonthlyReportFilterRequest filters, Pageable pageable) {
        Page<MonthlyReportResponse> response = service.getAll(filters, pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonthlyReportDetailedResponse> getById(@PathVariable Long id) {
        MonthlyReportDetailedResponse response = mapper.toDetailedResponse(service.getById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate")
    public ResponseEntity<MonthlyReportResponse> generate(@RequestParam Integer month, @RequestParam Integer year) {
        MonthlyReportResponse response = mapper.toResponse(service.generate(month, year));
        return ResponseEntity.ok(response);
    }
}
