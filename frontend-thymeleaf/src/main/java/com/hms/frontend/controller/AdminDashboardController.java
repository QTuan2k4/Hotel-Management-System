package com.hms.frontend.controller;

import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
    private final com.hms.frontend.api.GatewayApiClient apiClient;

    public AdminDashboardController(com.hms.frontend.api.GatewayApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model, RedirectAttributes ra) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn() || !auth.isAdmin()) {
            ra.addFlashAttribute("error", "Admin access required");
            return "redirect:/login";
        }
        model.addAttribute("auth", auth);

        // Fetch dashboard stats
        try {
           com.hms.frontend.api.dto.DashboardReportDTO stats = apiClient.get("/api/reports/dashboard", com.hms.frontend.api.dto.DashboardReportDTO.class, auth);
           if (stats != null) {
               model.addAttribute("stats", stats);
           }
        } catch (Exception e) {
            // ignore error, just show empty stats
        }
        return "admin/dashboard";
    }

    private SessionAuth getAuth(HttpSession session) {
        Object v = session.getAttribute("AUTH");
        return (v instanceof SessionAuth a) ? a : new SessionAuth();
    }
}

