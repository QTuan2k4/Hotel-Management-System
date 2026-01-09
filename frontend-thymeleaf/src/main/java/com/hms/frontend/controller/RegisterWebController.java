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
        RegisterRequest req = new RegisterRequest();
        req.setUsername(username);
        req.setPassword(password);
        req.setEmail(email);
        boolean success = false;
        try {
            success = api.postForStatus("/api/auth/register", req, null);
        } catch (Exception e) {
             // Extract message if possible ("Username already exists")
             String msg = e.getMessage();
             if (msg == null || msg.isBlank()) msg = "Registration failed. Username may already exist.";
             
             ra.addFlashAttribute("error", msg);
             return "redirect:/register";
        }

        if (!success) {
            ra.addFlashAttribute("error", "Registration failed. Username may already exist.");
            return "redirect:/register";
        }

        ra.addFlashAttribute("success", "Registration successful! Please login.");
        return "redirect:/login";
    }
}
