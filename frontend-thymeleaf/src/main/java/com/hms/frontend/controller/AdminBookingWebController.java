package com.hms.frontend.controller;

import com.hms.common.dto.booking.BookingDto;
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
@RequestMapping("/admin")
public class AdminBookingWebController {

    private final GatewayApiClient api;

    public AdminBookingWebController(GatewayApiClient api) {
        this.api = api;
    }

    @GetMapping("/bookings")
    public String listBookings(Model model, HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn() || !auth.isAdmin()) {
            ra.addFlashAttribute("error", "Admin access required");
            return "redirect:/login";
        }

        BookingDto[] arr = api.get("/api/admin/bookings", BookingDto[].class, auth);
        List<BookingDto> bookings = arr == null ? List.of() : Arrays.asList(arr);
        model.addAttribute("bookings", bookings);
        return "admin/bookings";
    }

    @PostMapping("/bookings/{id}/checkin")
    public String checkin(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn() || !auth.isAdmin())
            return "redirect:/login";
        api.post("/api/admin/bookings/" + id + "/checkin", null, Object.class, auth);
        ra.addFlashAttribute("success", "Guest checked in");
        return "redirect:/admin/bookings";
    }

    @PostMapping("/bookings/{id}/checkout")
    public String checkout(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn() || !auth.isAdmin())
            return "redirect:/login";
        api.post("/api/admin/bookings/" + id + "/checkout", null, Object.class, auth);
        ra.addFlashAttribute("success", "Guest checked out");
        return "redirect:/admin/bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancel(@PathVariable Long id, @RequestParam String reason, HttpSession session,
            RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn() || !auth.isAdmin())
            return "redirect:/login";

        // Construct body with reason
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("reason", reason);

        api.post("/api/admin/bookings/" + id + "/cancel", body, Object.class, auth);
        ra.addFlashAttribute("success", "Booking cancelled, invoice deleted and email sent.");
        return "redirect:/admin/bookings";
    }

    @GetMapping("/invoices")
    public String listInvoices(Model model, HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn() || !auth.isAdmin()) {
            ra.addFlashAttribute("error", "Admin access required");
            return "redirect:/login";
        }

        InvoiceDto[] arr = api.get("/api/admin/invoices", InvoiceDto[].class, auth);
        List<InvoiceDto> invoices = arr == null ? List.of() : Arrays.asList(arr);
        model.addAttribute("invoices", invoices);
        return "admin/invoices";
    }

    @PostMapping("/invoices/{id}/cash-paid")
    public String markCashPaid(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn() || !auth.isAdmin())
            return "redirect:/login";
        api.post("/api/admin/invoices/" + id + "/cash-paid", null, Object.class, auth);
        ra.addFlashAttribute("success", "Invoice marked as paid");
        return "redirect:/admin/invoices";
    }

    private SessionAuth getAuth(HttpSession session) {
        Object v = session.getAttribute("AUTH");
        return (v instanceof SessionAuth a) ? a : new SessionAuth();
    }
}
