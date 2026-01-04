package com.hms.booking.controller;

import com.hms.booking.service.BookingAppService;
import com.hms.common.dto.booking.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingAppService bookingAppService;

    public BookingController(BookingAppService bookingAppService) {
        this.bookingAppService = bookingAppService;
    }

    @GetMapping("/availability")
    public AvailabilityResponse availability(@RequestParam Long roomId,
                                             @RequestParam LocalDate from,
                                             @RequestParam LocalDate to) {
        return new AvailabilityResponse(bookingAppService.isAvailable(roomId, from, to));
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-User-Id") Long userId,
                             @Valid @RequestBody CreateBookingRequest req) {
        return bookingAppService.createBooking(userId, req);
    }

    @GetMapping("/my")
    public List<BookingDto> my(@RequestHeader("X-User-Id") Long userId) {
        return bookingAppService.myBookings(userId);
    }
}
