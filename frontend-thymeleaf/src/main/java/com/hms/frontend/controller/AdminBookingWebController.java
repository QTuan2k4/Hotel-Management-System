package com.hms.frontend.controller;

import com.hms.common.dto.booking.BookingDto;
import com.hms.common.dto.booking.BookingStatus;
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
@RequestMapping("/admin/bookings")
public class AdminBookingWebController {

    private final GatewayApiClient api;

    public AdminBookingWebController(GatewayApiClient api) {
        this.api = api;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String status,
                       Model model,
                       HttpSession session) {

        SessionAuth auth = (SessionAuth) session.getAttribute("AUTH");
        if (auth == null || !auth.isLoggedIn() || !auth.isAdmin()) {
            model.addAttribute("error", "Bạn cần quyền ADMIN");
            return "login";
        }

        try {
            String path = "/api/admin/bookings" + (status != null && !status.isBlank() ? "?status=" + status : "");
            BookingDto[] arr = api.get(path, BookingDto[].class, auth);
            List<BookingDto> bookings = arr == null ? List.of() : Arrays.asList(arr);

            model.addAttribute("bookings", bookings);
            model.addAttribute("status", status);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "admin/booking-list";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = (SessionAuth) session.getAttribute("AUTH");
        if (auth == null || !auth.isLoggedIn() || !auth.isAdmin()) {
            ra.addFlashAttribute("error", "Bạn cần quyền ADMIN");
            return "redirect:/login";
        }

        try {
            api.put("/api/admin/bookings/" + id + "/approve", null, BookingDto.class, auth);
            ra.addFlashAttribute("message", "Approve thành công booking #" + id);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = (SessionAuth) session.getAttribute("AUTH");
        if (auth == null || !auth.isLoggedIn() || !auth.isAdmin()) {
            ra.addFlashAttribute("error", "Bạn cần quyền ADMIN");
            return "redirect:/login";
        }

        try {
            api.put("/api/admin/bookings/" + id + "/reject", null, BookingDto.class, auth);
            ra.addFlashAttribute("message", "Reject thành công booking #" + id);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/bookings";
    }
}
