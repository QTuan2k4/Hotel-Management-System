package com.hms.frontend.controller;

import com.hms.common.dto.booking.BookingDto;
import com.hms.frontend.api.GatewayApiClient;
import com.hms.frontend.session.SessionAuth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class BookingPageController {

    private final GatewayApiClient api;

    public BookingPageController(GatewayApiClient api) {
        this.api = api;
    }

    // Submit booking từ trang room detail
    @PostMapping("/bookings")
    public String createBooking(@RequestParam Long roomId,
                                @RequestParam String checkInDate,
                                @RequestParam String checkOutDate,
                                RedirectAttributes ra,
                                HttpSession session) {
    	SessionAuth auth = (SessionAuth) session.getAttribute("AUTH");
        try {
            api.createBooking(roomId, checkInDate, checkOutDate, auth);
            ra.addFlashAttribute("message", "Đặt phòng thành công. Vui lòng chờ admin duyệt.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rooms/" + roomId;
    }

    @GetMapping("/bookings/my")
    public String myBookings(Model model, HttpSession session) {
    	SessionAuth auth = (SessionAuth) session.getAttribute("AUTH");
        try {
            BookingDto[] arr = api.getMyBookings(auth);
            model.addAttribute("bookings", arr == null ? java.util.List.of() : java.util.Arrays.asList(arr));
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "bookings/my";
    }

}
