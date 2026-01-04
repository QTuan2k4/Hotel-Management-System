package com.hms.booking.service;

import com.hms.booking.entity.Booking;
import com.hms.booking.repository.BookingRepository;
import com.hms.common.dto.booking.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingAppService {

    private final BookingRepository bookingRepository;

    private static final List<BookingStatus> HOLD_STATUSES = List.of(
            BookingStatus.PENDING_APPROVAL,
            BookingStatus.APPROVED,
            BookingStatus.CHECKED_IN
    );

    public BookingAppService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public boolean isAvailable(Long roomId, java.time.LocalDate from, java.time.LocalDate to) {
        return !bookingRepository.existsOverlap(roomId, from, to, HOLD_STATUSES);
    }

    @Transactional
    public BookingDto createBooking(Long userId, CreateBookingRequest req) {
        validateDates(req.getCheckInDate(), req.getCheckOutDate());

        boolean ok = isAvailable(req.getRoomId(), req.getCheckInDate(), req.getCheckOutDate());
        if (!ok) throw new IllegalArgumentException("Room not available for selected dates");

        Booking b = Booking.builder()
                .roomId(req.getRoomId())
                .userId(userId)
                .checkInDate(req.getCheckInDate())
                .checkOutDate(req.getCheckOutDate())
                .status(BookingStatus.PENDING_APPROVAL)
                .createdAt(LocalDateTime.now())
                .build();

        b = bookingRepository.save(b);
        return toDto(b);
    }

    public List<BookingDto> myBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    private void validateDates(java.time.LocalDate in, java.time.LocalDate out) {
        if (in == null || out == null) throw new IllegalArgumentException("Dates required");
        if (!out.isAfter(in)) throw new IllegalArgumentException("checkOutDate must be after checkInDate");
    }

    private BookingDto toDto(Booking b) {
        BookingDto dto = new BookingDto();
        dto.setId(b.getId());
        dto.setRoomId(b.getRoomId());
        dto.setUserId(b.getUserId());
        dto.setCheckInDate(b.getCheckInDate());
        dto.setCheckOutDate(b.getCheckOutDate());
        dto.setStatus(b.getStatus());
        dto.setPricePerNightSnapshot(b.getPricePerNightSnapshot());
        return dto;
    }

    
}
