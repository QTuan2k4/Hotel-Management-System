package com.hms.frontend.controller;

import com.hms.common.dto.auth.RegisterRequest;
import com.hms.frontend.api.GatewayApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterWebController {

    private final GatewayApiClient api;

    public RegisterWebController(GatewayApiClient api) {
        this.api = api;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String email,
            RedirectAttributes ra) {
        RegisterRequest req = new RegisterRequest(username, password, email);
        Object resp = api.post("/api/auth/register", req, Object.class, null);

        if (resp == null) {
            ra.addFlashAttribute("error", "Registration failed. Username may already exist.");
            return "redirect:/register";
        }

        ra.addFlashAttribute("success", "Registration successful! Please login.");
        return "redirect:/login";
    }
}
