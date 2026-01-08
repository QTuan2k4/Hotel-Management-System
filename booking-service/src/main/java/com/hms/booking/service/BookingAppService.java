package com.hms.booking.service;

import com.hms.booking.client.BillingClient;
import com.hms.booking.client.RoomClient;
import com.hms.booking.entity.Booking;
import com.hms.booking.repository.BookingRepository;
import com.hms.common.dto.billing.CreateInvoiceFromBookingRequest;
import com.hms.common.dto.booking.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookingAppService {

    private final BookingRepository bookingRepository;
    private final RoomClient roomClient;
    private final BillingClient billingClient;

    private static final List<BookingStatus> HOLD_STATUSES = List.of(
            BookingStatus.PENDING_APPROVAL,
            BookingStatus.APPROVED,
            BookingStatus.CONFIRMED,
            BookingStatus.CHECKED_IN);

    public BookingAppService(BookingRepository bookingRepository, RoomClient roomClient, BillingClient billingClient) {
        this.bookingRepository = bookingRepository;
        this.roomClient = roomClient;
        this.billingClient = billingClient;
    }

    /**
     * Get list of booked dates for a room in the next N days
     */
    public List<String> getBookedDates(Long roomId, int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(daysAhead);

        List<Booking> bookings = bookingRepository.findByRoomIdAndDateRange(roomId, today, endDate, HOLD_STATUSES);

        Set<String> bookedDates = new HashSet<>();
        for (Booking b : bookings) {
            LocalDate d = b.getCheckInDate();
            while (!d.isAfter(b.getCheckOutDate().minusDays(1))) {
                if (!d.isBefore(today) && !d.isAfter(endDate)) {
                    bookedDates.add(d.toString());
                }
                d = d.plusDays(1);
            }
        }
        return new ArrayList<>(bookedDates);
    }

    public boolean isAvailable(Long roomId, LocalDate from, LocalDate to) {
        return !bookingRepository.existsOverlap(roomId, from, to, HOLD_STATUSES);
    }

    @Transactional
    public BookingDto createBooking(Long userId, CreateBookingRequest req) {
        validateDates(req.getCheckInDate(), req.getCheckOutDate());

        boolean ok = isAvailable(req.getRoomId(), req.getCheckInDate(), req.getCheckOutDate());
        if (!ok)
            throw new IllegalArgumentException("Room not available for selected dates");

        // Get room price
        BigDecimal pricePerNight = roomClient.getPricePerNight(req.getRoomId());

        Booking b = Booking.builder()
                .roomId(req.getRoomId())
                .userId(userId)
                .checkInDate(req.getCheckInDate())
                .checkOutDate(req.getCheckOutDate())
                .status(BookingStatus.CONFIRMED) // Auto-confirm booking
                .pricePerNightSnapshot(pricePerNight)
                .createdAt(LocalDateTime.now())
                .build();

        b = bookingRepository.save(b);

        // Create invoice immediately
        CreateInvoiceFromBookingRequest invoiceReq = new CreateInvoiceFromBookingRequest();
        invoiceReq.setBookingId(b.getId());
        invoiceReq.setUserId(userId);
        invoiceReq.setRoomId(req.getRoomId());
        invoiceReq.setCheckInDate(req.getCheckInDate());
        invoiceReq.setCheckOutDate(req.getCheckOutDate());
        invoiceReq.setPricePerNight(pricePerNight);

        billingClient.createInvoiceFromBooking(invoiceReq);

        return toDto(b);
    }

    public List<BookingDto> myBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    private void validateDates(LocalDate in, LocalDate out) {
        if (in == null || out == null)
            throw new IllegalArgumentException("Dates required");
        if (!out.isAfter(in))
            throw new IllegalArgumentException("checkOutDate must be after checkInDate");
        if (in.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Cannot book past dates");
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
