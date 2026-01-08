package com.hms.booking.controller;

import com.hms.booking.service.BookingAppService;
import com.hms.common.dto.booking.AvailabilityResponse;
import com.hms.common.dto.booking.BookingDto;
import com.hms.common.dto.booking.CreateBookingRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingAppService bookingService;

    public BookingController(BookingAppService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestBody CreateBookingRequest req,
            @RequestHeader("X-User-Id") Long userId) {
        return bookingService.createBooking(userId, req);
    }

    @GetMapping("/my")
    public List<BookingDto> my(@RequestHeader("X-User-Id") Long userId) {
        return bookingService.myBookings(userId);
    }

    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        boolean available = bookingService.isAvailable(roomId, checkIn, checkOut);
        String msg = available ? "Room is available" : "Room is not available for selected dates";
        return ResponseEntity.ok(new AvailabilityResponse(available, msg));
    }

    /**
     * Get list of booked dates for a room (next 15 days)
     * This is a public endpoint - no auth required
     */
    @GetMapping("/booked-dates/{roomId}")
    public List<String> getBookedDates(@PathVariable Long roomId) {
        return bookingService.getBookedDates(roomId, 15);
    }

    @GetMapping("/ping")
    public String ping() {
        return "booking-service: OK";
    }
}
