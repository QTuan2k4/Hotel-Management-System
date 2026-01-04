package com.hms.frontend.controller;

import com.hms.common.dto.auth.AuthResponse;
import com.hms.common.dto.auth.LoginRequest;
import com.hms.frontend.api.GatewayApiClient;
import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthWebController {

    private final GatewayApiClient api;

    public AuthWebController(GatewayApiClient api) {
        this.api = api;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@Valid @ModelAttribute("loginRequest") LoginRequest req,
                          BindingResult br,
                          HttpSession session,
                          Model model) {
        if (br.hasErrors()) return "login";
        try {
            AuthResponse resp = api.post("/api/auth/login", req, AuthResponse.class, null);
            SessionAuth auth = new SessionAuth();
            auth.setToken(resp.getAccessToken());
            auth.setUserId(resp.getUserId());
            auth.setUsername(req.getUsername());
            auth.setRoles(resp.getRoles());
            session.setAttribute("AUTH", auth);
            return "redirect:/";
        } catch (Exception ex) {
            model.addAttribute("error", "Login thất bại: " + ex.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
