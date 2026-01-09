package com.hms.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "booking-service", url = "${application.config.booking-url}")
public interface BookingClient {
    
    @GetMapping("/internal/bookings/stats/count")
    long countTotalBookings();
    
    // Future: @GetMapping("/internal/stats/today")
}
