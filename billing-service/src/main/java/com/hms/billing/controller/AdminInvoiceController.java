package com.hms.billing.controller;

import com.hms.billing.service.InvoiceService;
import com.hms.common.dto.billing.InvoiceDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/invoices")
public class AdminInvoiceController {

    private final InvoiceService invoiceService;

    public AdminInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<InvoiceDto> list() {
        return invoiceService.adminList();
    }

    @PostMapping("/{id}/cash-paid")
    public InvoiceDto markCashPaid(@PathVariable Long id) {
        return invoiceService.markCashPaid(id);
    }
}
