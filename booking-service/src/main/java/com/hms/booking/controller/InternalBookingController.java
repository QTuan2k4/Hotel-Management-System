package com.hms.booking.controller;

import com.hms.booking.entity.Booking;
import com.hms.booking.repository.BookingRepository;
import com.hms.booking.service.AdminBookingService;
import com.hms.common.dto.booking.BookingStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal API for service-to-service communication
 */
@RestController
@RequestMapping("/api/internal/bookings")
public class InternalBookingController {

    private final BookingRepository repo;
    private final AdminBookingService adminBookingService;
    private final com.hms.booking.client.NotificationClient notificationClient;

    public InternalBookingController(BookingRepository repo, AdminBookingService adminBookingService, com.hms.booking.client.NotificationClient notificationClient) {
        this.repo = repo;
        this.adminBookingService = adminBookingService;
        this.notificationClient = notificationClient;
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

        // Always ensure confirmed if paid
        if (b.getStatus() != BookingStatus.CONFIRMED && b.getStatus() != BookingStatus.CHECKED_IN && b.getStatus() != BookingStatus.CHECKED_OUT) {
             b.setStatus(BookingStatus.CONFIRMED);
             repo.save(b);
             
             // NOTIFY USER
             try {
                 notificationClient.notifyUser(
                     b.getUserId(),
                     "PAYMENT_RECEIVED",
                     "Payment Successful!",
                     "We received your payment. Your booking #" + bookingId + " is now Confirmed.",
                     b.getId()
                 );
             } catch (Exception e) {
                 System.err.println("Failed to notify user: " + e.getMessage());
             }

             // NOTIFY ADMIN
             try {
                 notificationClient.notifyAdmin(
                     "PAYMENT_RECEIVED",
                     "Payment Received",
                     "Booking #" + bookingId + " has been paid via VNPay.",
                     b.getId()
                 );
             } catch (Exception e) {
                 System.err.println("Failed to notify admin: " + e.getMessage());
             }
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Cancel a booking due to payment failure
     */
    @PostMapping("/{bookingId}/cancel-payment-failed")
    public ResponseEntity<Void> cancelDueToPaymentFailure(@PathVariable Long bookingId) {
        try {
            adminBookingService.cancel(bookingId, 0L, "Thanh toan that bai / Payment failed");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Failed to cancel booking: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats/count")
    public long countAll() {
        return repo.count();
    }

    /**
     * Count bookings by year and optional month (exact calendar dates)
     * If only year: Jan 1 to Dec 31
     * If year + month: 1st to last day of that month
     */
    @GetMapping("/stats/count-by-date")
    public long countByDate(@RequestParam int year,
            @RequestParam(required = false) Integer month) {
        java.time.LocalDateTime startDate;
        java.time.LocalDateTime endDate;

        if (month != null && month >= 1 && month <= 12) {
            // Filter by specific month
            java.time.YearMonth yearMonth = java.time.YearMonth.of(year, month);
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        } else {
            // Filter by entire year
            startDate = java.time.LocalDateTime.of(year, 1, 1, 0, 0, 0);
            endDate = java.time.LocalDateTime.of(year, 12, 31, 23, 59, 59);
        }

        return repo.countByCreatedAtBetween(startDate, endDate);
    }
}
