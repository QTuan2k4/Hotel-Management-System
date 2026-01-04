package com.hms.booking.controller;

import com.hms.booking.service.AdminBookingService;
import com.hms.common.dto.booking.BookingDto;
import com.hms.common.dto.booking.BookingStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    private final AdminBookingService service;

    public AdminBookingController(AdminBookingService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookingDto> list(@RequestParam(required = false) BookingStatus status) {
        return service.list(status);
    }

    @PutMapping("/{id}/approve")
    public BookingDto approve(@PathVariable Long id,
                              @RequestHeader("X-User-Id") Long adminId) {
        return service.approve(id, adminId);
    }

    @PutMapping("/{id}/reject")
    public BookingDto reject(@PathVariable Long id,
                             @RequestHeader("X-User-Id") Long adminId) {
        return service.reject(id, adminId);
    }
}
