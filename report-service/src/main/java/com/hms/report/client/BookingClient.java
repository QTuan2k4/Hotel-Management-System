package com.hms.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "booking-service", url = "${application.config.booking-url}")
public interface BookingClient {

    @GetMapping("/api/internal/bookings/stats/count")
    long countTotalBookings();

    @GetMapping("/api/internal/bookings/stats/count-by-date")
    long countByDate(@RequestParam("year") int year,
            @RequestParam(value = "month", required = false) Integer month);
}
