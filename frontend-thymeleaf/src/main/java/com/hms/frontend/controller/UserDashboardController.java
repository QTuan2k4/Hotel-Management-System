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

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        SessionAuth auth = getAuth(session);
        
        if (!auth.isLoggedIn()) {
            return "redirect:/login";
        }
        
        model.addAttribute("username", auth.getUsername());
        return "user/dashboard";
    }

    private SessionAuth getAuth(HttpSession session) {
        Object v = session.getAttribute("AUTH");
        return (v instanceof SessionAuth a) ? a : new SessionAuth();
    }
}
