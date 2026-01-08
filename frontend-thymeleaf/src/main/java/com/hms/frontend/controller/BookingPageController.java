package com.hms.frontend.controller;

import com.hms.common.dto.booking.BookingDto;
import com.hms.common.dto.booking.CreateBookingRequest;
import com.hms.common.dto.billing.InvoiceDto;
import com.hms.common.dto.payment.CreateVnpayPaymentRequest;
import com.hms.common.dto.payment.CreateVnpayPaymentResponse;
import com.hms.frontend.api.GatewayApiClient;
import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
public class BookingPageController {

    private final GatewayApiClient api;

    public BookingPageController(GatewayApiClient api) {
        this.api = api;
    }

    /**
     * Proxy endpoint for getting booked dates from booking-service via gateway
     */
    @GetMapping("/api/bookings/booked-dates/{roomId}")
    @ResponseBody
    public List<String> getBookedDates(@PathVariable Long roomId) {
        String[] dates = api.getPublic("/api/bookings/booked-dates/" + roomId, String[].class);
        return dates == null ? List.of() : Arrays.asList(dates);
    }

    @PostMapping("/bookings/create")
    public String createBooking(@RequestParam Long roomId,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            HttpSession session,
            RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn()) {
            ra.addFlashAttribute("error", "Please login to book a room");
            return "redirect:/login";
        }

        CreateBookingRequest req = new CreateBookingRequest();
        req.setRoomId(roomId);
        req.setCheckInDate(LocalDate.parse(checkInDate));
        req.setCheckOutDate(LocalDate.parse(checkOutDate));

        BookingDto resp = api.post("/api/bookings", req, BookingDto.class, auth);
        if (resp == null) {
            ra.addFlashAttribute("error", "Booking failed. Room may not be available for selected dates.");
            return "redirect:/rooms/" + roomId;
        }

        ra.addFlashAttribute("success", "Booking confirmed! Invoice created. Please proceed to payment.");
        return "redirect:/invoices/my";
    }

    @GetMapping("/bookings/my")
    public String myBookings(Model model, HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn()) {
            ra.addFlashAttribute("error", "Please login first");
            return "redirect:/login";
        }

        BookingDto[] arr = api.get("/api/bookings/my", BookingDto[].class, auth);
        List<BookingDto> bookings = arr == null ? List.of() : Arrays.asList(arr);
        model.addAttribute("bookings", bookings);
        return "bookings/my";
    }

    @GetMapping("/invoices/my")
    public String myInvoices(Model model, HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn()) {
            ra.addFlashAttribute("error", "Please login first");
            return "redirect:/login";
        }

        InvoiceDto[] arr = api.get("/api/invoices/my", InvoiceDto[].class, auth);
        List<InvoiceDto> invoices = arr == null ? List.of() : Arrays.asList(arr);
        model.addAttribute("invoices", invoices);
        return "invoices/my";
    }

    @PostMapping("/invoices/{id}/pay")
    public String payInvoice(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn())
            return "redirect:/login";

        CreateVnpayPaymentRequest req = new CreateVnpayPaymentRequest(id);
        CreateVnpayPaymentResponse resp = api.post("/api/payments/vnpay/create", req, CreateVnpayPaymentResponse.class,
                auth);

        if (resp != null && resp.getPaymentUrl() != null) {
            return "redirect:" + resp.getPaymentUrl();
        }

        ra.addFlashAttribute("error", "Payment initiation failed");
        return "redirect:/invoices/my";
    }

    @GetMapping("/payment/result")
    public String paymentResult(@RequestParam(required = false) String status,
            @RequestParam(required = false) String txnRef,
            Model model) {
        model.addAttribute("status", status);
        model.addAttribute("txnRef", txnRef);
        return "payment/result";
    }

    private SessionAuth getAuth(HttpSession session) {
        Object v = session.getAttribute("AUTH");
        return (v instanceof SessionAuth a) ? a : new SessionAuth();
    }
}
