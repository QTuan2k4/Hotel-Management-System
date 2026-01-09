package com.hms.booking.controller;

import com.hms.booking.entity.Booking;
import com.hms.booking.repository.BookingRepository;
import com.hms.common.dto.booking.BookingStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal API for service-to-service communication
 */
@RestController
@RequestMapping("/internal/bookings")
public class InternalBookingController {

    private final BookingRepository repo;

    public InternalBookingController(BookingRepository repo) {
        this.repo = repo;
    }

    /**
     * Confirm a booking after payment is successful
     */
    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<Void> confirmBooking(@PathVariable Long bookingId) {
        Booking b = repo.findById(bookingId).orElse(null);
        if (b == null) {
            return ResponseEntity.notFound().build();
        }

        // Only confirm if pending
        if (b.getStatus() == BookingStatus.PENDING_APPROVAL) {
            b.setStatus(BookingStatus.CONFIRMED);
            repo.save(b);
        }

        return ResponseEntity.ok().build();
    }
    @GetMapping("/stats/count")
    public long countAll() {
        return repo.count();
    }
}
