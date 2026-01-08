package com.hms.booking.controller;

import com.hms.booking.service.AdminBookingService;
import com.hms.common.dto.booking.BookingDto;
import com.hms.common.dto.booking.BookingStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    private final AdminBookingService adminService;

    public AdminBookingController(AdminBookingService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public List<BookingDto> list(@RequestParam(required = false) BookingStatus status) {
        return adminService.list(status);
    }

    @PostMapping("/{id}/checkin")
    public BookingDto checkin(@PathVariable Long id,
            @RequestHeader("X-User-Id") Long adminId) {
        return adminService.checkin(id, adminId);
    }

    @PostMapping("/{id}/checkout")
    public BookingDto checkout(@PathVariable Long id,
            @RequestHeader("X-User-Id") Long adminId) {
        return adminService.checkout(id, adminId);
    }

    @PostMapping("/{id}/cancel")
    public BookingDto cancel(@PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") Long adminId) {
        String reason = body.getOrDefault("reason", "Cancelled by admin");
        return adminService.cancel(id, adminId, reason);
    }
}
