package com.hms.booking.service;

import com.hms.booking.client.RoomClient;
import com.hms.booking.entity.Booking;
import com.hms.booking.repository.BookingRepository;
import com.hms.common.dto.booking.BookingDto;
import com.hms.common.dto.booking.BookingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminBookingService {

    private final BookingRepository repo;
    private final RoomClient roomClient;

    public AdminBookingService(BookingRepository repo, RoomClient roomClient) {
        this.repo = repo;
        this.roomClient = roomClient;
    }

    public List<BookingDto> list(BookingStatus status) {
        List<Booking> bookings = (status == null)
                ? repo.findAllByOrderByCreatedAtDesc()
                : repo.findByStatusOrderByCreatedAtDesc(status);

        return bookings.stream().map(this::toDto).toList();
    }

    @Transactional
    public BookingDto approve(Long bookingId, Long adminId) {
        Booking b = repo.findById(bookingId).orElseThrow();

        if (b.getStatus() != BookingStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only PENDING_APPROVAL can be approved");
        }

        // chốt giá tại lúc duyệt
        BigDecimal price = roomClient.getPricePerNight(b.getRoomId());
        b.setPricePerNightSnapshot(price);

        b.setStatus(BookingStatus.APPROVED);
        b.setApprovedAt(LocalDateTime.now());
        b.setApprovedBy(adminId);

        return toDto(b);
    }

    @Transactional
    public BookingDto reject(Long bookingId, Long adminId) {
        Booking b = repo.findById(bookingId).orElseThrow();

        if (b.getStatus() != BookingStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only PENDING_APPROVAL can be rejected");
        }

        b.setStatus(BookingStatus.REJECTED);
        b.setApprovedAt(LocalDateTime.now());
        b.setApprovedBy(adminId);

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
