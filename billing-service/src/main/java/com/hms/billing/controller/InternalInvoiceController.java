package com.hms.billing.controller;

import com.hms.billing.service.InvoiceService;
import com.hms.common.dto.billing.CreateInvoiceFromBookingRequest;
import com.hms.common.dto.billing.InvoiceDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/invoices")
public class InternalInvoiceController {

    private final InvoiceService invoiceService;

    public InternalInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/from-booking")
    public InvoiceDto createFromBooking(@RequestBody CreateInvoiceFromBookingRequest req) {
        return invoiceService.createFromBooking(req);
    }

    @PostMapping("/{id}/mark-paid")
    public InvoiceDto markPaid(@PathVariable Long id) {
        return invoiceService.markPaidInternal(id);
    }

    @GetMapping("/{id}")
    public InvoiceDto get(@PathVariable Long id) {
        return invoiceService.get(id);
    }

    @DeleteMapping("/by-booking/{bookingId}")
    public void deleteByBookingId(@PathVariable Long bookingId) {
        invoiceService.deleteByBookingId(bookingId);
    }

    @GetMapping("/stats/revenue")
    public java.math.BigDecimal getRevenue() {
        return invoiceService.getTotalRevenue(); // Service method needs to be created too? Or just use repo here for speed?
        // Better to use service. I'll check if I can just inject Repository here or if I should add to service.
        // The controller injects InvoiceService.
        // I should add method to InvoiceService.
    }
}
