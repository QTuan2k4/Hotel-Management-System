package com.hms.booking.repository;

import com.hms.booking.entity.Booking;
import com.hms.common.dto.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
        select (count(b) > 0) from Booking b
        where b.roomId = :roomId
          and b.status in :holdStatuses
          and (b.checkInDate < :toDate and b.checkOutDate > :fromDate)
    """)
    boolean existsOverlap(Long roomId, LocalDate fromDate, LocalDate toDate, List<BookingStatus> holdStatuses);
    
    List<Booking> findAllByOrderByCreatedAtDesc();
    List<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status);

}
