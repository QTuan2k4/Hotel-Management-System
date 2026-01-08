package com.hms.billing.controller;

import com.hms.billing.service.InvoiceService;
import com.hms.common.dto.billing.InvoiceDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/my")
    public List<InvoiceDto> my(@RequestHeader("X-User-Id") Long userId) {
        return invoiceService.myInvoices(userId);
    }

    @GetMapping("/{id}")
    public InvoiceDto get(@PathVariable Long id) {
        return invoiceService.get(id);
    }
}
