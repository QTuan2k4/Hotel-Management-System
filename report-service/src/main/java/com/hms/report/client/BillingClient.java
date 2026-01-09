package com.hms.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@FeignClient(name = "billing-service", url = "${application.config.billing-url}")
public interface BillingClient {

    @GetMapping("/api/internal/invoices/stats/revenue")
    BigDecimal getTotalRevenue();

    @GetMapping("/api/internal/invoices/stats/revenue-by-date")
    BigDecimal getRevenueByDate(@RequestParam("year") int year,
            @RequestParam(value = "month", required = false) Integer month);
}
