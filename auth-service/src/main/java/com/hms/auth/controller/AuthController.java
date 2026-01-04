package com.hms.auth.controller;

import com.hms.auth.service.UserService;
import com.hms.common.dto.auth.AuthResponse;
import com.hms.common.dto.auth.LoginRequest;
import com.hms.common.dto.auth.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        userService.register(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return userService.login(req);
    }

    @GetMapping("/ping")
    public String ping() {
        return "auth-service: OK";
    }
}
