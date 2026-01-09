package com.hms.frontend.controller;

import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    private final com.hms.frontend.api.GatewayApiClient gatewayApiClient;

    public UserDashboardController(com.hms.frontend.api.GatewayApiClient gatewayApiClient) {
        this.gatewayApiClient = gatewayApiClient;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        SessionAuth auth = getAuth(session);
        
        if (!auth.isLoggedIn()) {
            return "redirect:/login";
        }
        
        try {
            com.hms.common.dto.UserDto userProfile = gatewayApiClient.get("/api/auth/profile", com.hms.common.dto.UserDto.class, auth);
            model.addAttribute("userProfile", userProfile);
        } catch (Exception e) {
            // If fail, just show basic info
        }
        
        model.addAttribute("username", auth.getUsername());
        model.addAttribute("auth", auth);
        return "user/dashboard";
    }

    @org.springframework.web.bind.annotation.PostMapping("/profile")
    public String updateProfile(com.hms.common.dto.auth.UpdateProfileRequest req, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn()) {
            return "redirect:/login";
        }

        try {
            // Call API
            // Path: /api/auth/profile
            com.hms.common.dto.UserDto updatedUser = gatewayApiClient.put("/api/auth/profile", req, com.hms.common.dto.UserDto.class, auth);
            
            if (updatedUser != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
                // Optionally update session auth claims if email is stored there?
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/user/dashboard";
    }

    @org.springframework.web.bind.annotation.PostMapping("/password")
    public String changePassword(com.hms.common.dto.auth.ChangePasswordRequest req, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        SessionAuth auth = getAuth(session);
        if (!auth.isLoggedIn()) {
            return "redirect:/login";
        }
        
        if (!req.getNewPassword().equals(req.getNewPassword())) { 
             // Ideally validate confirm password in frontend or here if we had a confirm field in DTO (we don't)
             // But UI form usually has 'confirm' field which we compare.
             // Let's assume DTO only has old/new.
             // We will handle confirm check in UI JS or bind a specific Form object here.
        }

        try {
            // Path: /api/auth/password
            // Using generic Object or Void response
            gatewayApiClient.put("/api/auth/password", req, Void.class, auth);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password. check old password.");
        }

        return "redirect:/user/dashboard";
    }

    private SessionAuth getAuth(HttpSession session) {
        Object v = session.getAttribute("AUTH");
        return (v instanceof SessionAuth a) ? a : new SessionAuth();
    }
}
