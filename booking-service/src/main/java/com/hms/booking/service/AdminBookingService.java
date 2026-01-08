package com.hms.booking.service;

import com.hms.booking.client.BillingClient;
import com.hms.booking.entity.Booking;
import com.hms.booking.repository.BookingRepository;
import com.hms.common.dto.booking.BookingDto;
import com.hms.common.dto.booking.BookingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminBookingService {

    private final BookingRepository repo;
    private final BillingClient billingClient;
    private final com.hms.booking.client.NotificationClient notificationClient;
    private final com.hms.booking.client.UserClient userClient;

    public AdminBookingService(BookingRepository repo, BillingClient billingClient,
            com.hms.booking.client.NotificationClient notificationClient,
            com.hms.booking.client.UserClient userClient) {
        this.repo = repo;
        this.billingClient = billingClient;
        this.notificationClient = notificationClient;
        this.userClient = userClient;
    }

    public List<BookingDto> list(BookingStatus status) {
        List<Booking> bookings = (status == null)
                ? repo.findAllByOrderByCreatedAtDesc()
                : repo.findByStatusOrderByCreatedAtDesc(status);

        return bookings.stream().map(this::toDto).toList();
    }

    @Transactional
    public BookingDto checkin(Long bookingId, Long adminId) {
        Booking b = repo.findById(bookingId).orElseThrow();

        // Allow check-in for Confirmed or Approved (legacy)
        if (b.getStatus() != BookingStatus.CONFIRMED && b.getStatus() != BookingStatus.APPROVED)
            throw new IllegalStateException("Only CONFIRMED bookings can check-in");

        b.setStatus(BookingStatus.CHECKED_IN);
        b.setCheckedInAt(LocalDateTime.now());
        // No invoice creation here - it's done at booking time

        return toDto(b);
    }

    @Transactional
    public BookingDto checkout(Long bookingId, Long adminId) {
        Booking b = repo.findById(bookingId).orElseThrow();
        if (b.getStatus() != BookingStatus.CHECKED_IN)
            throw new IllegalStateException("Only CHECKED_IN can checkout");

        b.setStatus(BookingStatus.CHECKED_OUT);
        b.setCheckedOutAt(LocalDateTime.now());
        return toDto(b);
    }

    @Transactional
    public BookingDto cancel(Long bookingId, Long adminId, String reason) {
        Booking b = repo.findById(bookingId).orElseThrow();

        // Only allow cancellation of CONFIRMED/APPROVED bookings
        if (b.getStatus() != BookingStatus.CONFIRMED && b.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalStateException("Cannot cancel booking with status: " + b.getStatus());
        }

        b.setStatus(BookingStatus.CANCELLED);

        // Delete associated invoice
        try {
            billingClient.deleteInvoiceByBookingId(bookingId);
        } catch (Exception e) {
            System.err.println("Failed to delete invoice for cancelled booking: " + bookingId);
        }

        // Send email notification
        try {
            com.hms.booking.client.UserClient.UserDto user = userClient.getUserById(b.getUserId());
            if (user != null) {
                String toEmail = user.getEmail();
                if (toEmail == null || toEmail.isEmpty())
                    toEmail = user.getUsername(); // fallback if email field is empty

                String subject = "Booking Cancelled - #" + bookingId;
                String body = String.format("""
                        Dear %s,

                        Your booking #%d has been CANCELLED.
                        Reason: %s

                        Please contact Zalo 0393459773 for refund processing (amount in invoice).

                        Regards,
                        Hotel Management
                        """, user.getFullName() != null ? user.getFullName() : "Customer", bookingId, reason);

                notificationClient.sendEmail(toEmail, subject, body);
            } else {
                System.err.println("Could not find user info for userId: " + b.getUserId());
            }
        } catch (Exception e) {
            System.err.println("Failed to send cancellation email: " + e.getMessage());
            e.printStackTrace();
        }

        return toDto(b);
    }

    private BookingDto toDto(Booking b) {
        BookingDto dto = new BookingDto();
        dto.setId(b.getId());
        dto.setRoomId(b.getRoomId());
        dto.setUserId(b.getUserId());
        dto.setCheckInDate(b.getCheckInDate());
        dto.setCheckOutDate(b.getCheckOutDate());
        dto.setStatus(b.getStatus());
        dto.setCreatedAt(b.getCreatedAt());
        dto.setPricePerNightSnapshot(b.getPricePerNightSnapshot());
        return dto;
    }
}
