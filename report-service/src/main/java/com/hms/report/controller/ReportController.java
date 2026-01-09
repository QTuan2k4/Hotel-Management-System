package com.hms.report.controller;

import com.hms.report.dto.DashboardReport;
import com.hms.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardReport> getDashboardReport() {
        return ResponseEntity.ok(reportService.getDashboardReport());
    }

    @GetMapping("/dashboard/by-date")
    public ResponseEntity<DashboardReport> getDashboardReportByDate(
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(reportService.getDashboardReportByDate(year, month));
    }
}
