package com.hms.frontend.controller;

import com.hms.common.dto.auth.AuthResponse;
import com.hms.common.dto.auth.LoginRequest;
import com.hms.frontend.api.GatewayApiClient;
import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthWebController {

    private final GatewayApiClient api;

    public AuthWebController(GatewayApiClient api) {
        this.api = api;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes ra) {
        LoginRequest req = new LoginRequest(username, password);
        AuthResponse resp = api.post("/api/auth/login", req, AuthResponse.class, null);

        if (resp == null || resp.getToken() == null) {
            ra.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }

        SessionAuth auth = new SessionAuth();
        auth.setToken(resp.getToken());
        auth.setUserId(resp.getUserId());
        auth.setUsername(username);
        auth.setRoles(resp.getRoles());
        auth.setLoggedIn(true);
        session.setAttribute("AUTH", auth);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
