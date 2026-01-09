package com.hms.frontend.controller;

import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
    private final com.hms.frontend.api.GatewayApiClient apiClient;

    public AdminDashboardController(com.hms.frontend.api.GatewayApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping
    public String dashboard(@RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpSession session, Model model, RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn() || !auth.isAdmin()) {
            ra.addFlashAttribute("error", "Admin access required");
            return "redirect:/login";
        }
        model.addAttribute("auth", auth);

        // Generate year options (current year and 5 years back)
        int currentYear = Year.now().getValue();
        List<Integer> yearOptions = new ArrayList<>();
        for (int i = currentYear; i >= currentYear - 5; i--) {
            yearOptions.add(i);
        }
        model.addAttribute("yearOptions", yearOptions);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);

        // Fetch dashboard stats
        try {
            String apiUrl = "/api/reports/dashboard";
            if (year != null) {
                apiUrl = "/api/reports/dashboard/by-date?year=" + year;
                if (month != null) {
                    apiUrl += "&month=" + month;
                }
            }
            com.hms.frontend.api.dto.DashboardReportDTO stats = apiClient.get(apiUrl,
                    com.hms.frontend.api.dto.DashboardReportDTO.class, auth);
            if (stats != null) {
                model.addAttribute("stats", stats);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch dashboard stats: " + e.getMessage());
        }
        return "admin/dashboard";
    }

    private SessionAuth getAuth(HttpSession session) {
        Object v = session.getAttribute("AUTH");
        return (v instanceof SessionAuth a) ? a : new SessionAuth();
    }
}
