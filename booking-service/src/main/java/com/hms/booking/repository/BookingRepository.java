package com.hms.booking.repository;

import com.hms.booking.entity.Booking;
import com.hms.common.dto.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

        List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

        List<Booking> findAllByOrderByCreatedAtDesc();

        List<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status);

        @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
                        "WHERE b.roomId = :roomId " +
                        "AND b.status IN :statuses " +
                        "AND b.checkInDate < :checkOut " +
                        "AND b.checkOutDate > :checkIn")
        boolean existsOverlap(@Param("roomId") Long roomId,
                        @Param("checkIn") LocalDate checkIn,
                        @Param("checkOut") LocalDate checkOut,
                        @Param("statuses") List<BookingStatus> statuses);

        @Query("SELECT b FROM Booking b " +
                        "WHERE b.roomId = :roomId " +
                        "AND b.status IN :statuses " +
                        "AND b.checkOutDate > :fromDate " +
                        "AND b.checkInDate <= :toDate")
        List<Booking> findByRoomIdAndDateRange(
                        @Param("roomId") Long roomId,
                        @Param("fromDate") LocalDate fromDate,
                        @Param("toDate") LocalDate toDate,
                        @Param("statuses") List<BookingStatus> statuses);

        long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
