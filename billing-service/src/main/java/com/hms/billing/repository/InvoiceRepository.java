package com.hms.billing.repository;

import com.hms.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Invoice> findByBookingId(Long bookingId);

    @Query("SELECT SUM(i.total) FROM Invoice i WHERE i.status = com.hms.common.dto.billing.InvoiceStatus.PAID")
    java.math.BigDecimal sumTotalPaid();

    @Query("SELECT SUM(i.total) FROM Invoice i WHERE i.status = com.hms.common.dto.billing.InvoiceStatus.PAID AND i.paidAt >= :startDate AND i.paidAt <= :endDate")
    java.math.BigDecimal sumTotalPaidBetween(
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);
}
