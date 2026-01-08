package com.hms.frontend.controller;

import com.hms.common.dto.billing.InvoiceDto;
import com.hms.frontend.api.GatewayApiClient;
import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/invoices")
public class AdminInvoiceWebController {

    private final GatewayApiClient api;

    public AdminInvoiceWebController(GatewayApiClient api) {
        this.api = api;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        SessionAuth auth = (SessionAuth) session.getAttribute("AUTH");
        if (auth == null || !auth.isLoggedIn() || !auth.isAdmin()) {
            model.addAttribute("error", "Bạn cần quyền ADMIN");
            return "auth/login";
        }

        try {
            InvoiceDto[] arr = api.get("/api/admin/invoices", InvoiceDto[].class, auth);
            List<InvoiceDto> invoices = arr == null ? List.of() : Arrays.asList(arr);
            model.addAttribute("invoices", invoices);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("invoices", List.of());
        }

        return "admin/invoices";
    }

    @PostMapping("/{id}/cash-paid")
    public String markCashPaid(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes ra) {
        SessionAuth auth = (SessionAuth) session.getAttribute("AUTH");
        if (auth == null || !auth.isLoggedIn() || !auth.isAdmin()) {
            ra.addFlashAttribute("error", "Bạn cần quyền ADMIN");
            return "redirect:/login";
        }

        try {
            api.put("/api/admin/invoices/" + id + "/mark-cash-paid", null, InvoiceDto.class, auth);
            ra.addFlashAttribute("message", "Đã mark CASH PAID invoice #" + id);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/admin/invoices";
    }
}
