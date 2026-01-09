package com.hms.billing.service;

import com.hms.billing.entity.Invoice;
import com.hms.billing.repository.InvoiceRepository;
import com.hms.common.dto.billing.CreateInvoiceFromBookingRequest;
import com.hms.common.dto.billing.InvoiceDto;
import com.hms.common.dto.billing.InvoiceStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository repo;

    public InvoiceService(InvoiceRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public InvoiceDto createFromBooking(CreateInvoiceFromBookingRequest req) {
        // Idempotent: 1 booking = 1 invoice
        Invoice existing = repo.findByBookingId(req.getBookingId()).orElse(null);
        if (existing != null)
            return toDto(existing);

        long nights = ChronoUnit.DAYS.between(req.getCheckInDate(), req.getCheckOutDate());
        if (nights <= 0)
            throw new IllegalArgumentException("Invalid nights");

        BigDecimal total = req.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Invoice inv = Invoice.builder()
                .bookingId(req.getBookingId())
                .userId(req.getUserId())
                .roomId(req.getRoomId())
                .nights(nights)
                .pricePerNight(req.getPricePerNight())
                .total(total)
                .status(InvoiceStatus.PENDING_PAYMENT)
                .createdAt(LocalDateTime.now())
                .build();

        inv = repo.save(inv);
        return toDto(inv);
    }

    public List<InvoiceDto> myInvoices(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    public InvoiceDto get(Long id) {
        return toDto(repo.findById(id).orElseThrow());
    }

    public List<InvoiceDto> adminList() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public InvoiceDto markPaidInternal(Long id) {
        Invoice inv = repo.findById(id).orElseThrow();
        if (inv.getStatus() == InvoiceStatus.PAID)
            return toDto(inv);

        inv.setStatus(InvoiceStatus.PAID);
        inv.setPaidAt(LocalDateTime.now());
        return toDto(inv);
    }

    @Transactional
    public InvoiceDto markCashPaid(Long id) {
        return markPaidInternal(id);
    }

    @Transactional
    public void deleteByBookingId(Long bookingId) {
        Invoice inv = repo.findByBookingId(bookingId).orElse(null);
        if (inv != null) {
            // Only delete if pending payment (optional rule, but safer)
            if (inv.getStatus() == InvoiceStatus.PENDING_PAYMENT) {
                repo.delete(inv);
            } else {
                // If Paid, maybe refund logic here? For now, we allow delete or might throw
                // exception
                // "Cannot delete paid invoice" - but requirement says delete invoice.
                // Assuming we force delete.
                repo.delete(inv);
            }
        }
    }

    public BigDecimal getTotalRevenue() {
        return repo.sumTotalPaid();
    }

    private InvoiceDto toDto(Invoice inv) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(inv.getId());
        dto.setBookingId(inv.getBookingId());
        dto.setUserId(inv.getUserId());
        dto.setRoomId(inv.getRoomId());
        dto.setNights(inv.getNights());
        dto.setPricePerNight(inv.getPricePerNight());
        dto.setTotal(inv.getTotal());
        dto.setStatus(inv.getStatus());
        dto.setCreatedAt(inv.getCreatedAt());
        dto.setPaidAt(inv.getPaidAt());
        return dto;
    }
}
