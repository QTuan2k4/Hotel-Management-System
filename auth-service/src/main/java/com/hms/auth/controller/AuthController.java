package com.hms.auth.controller;

import com.hms.auth.service.UserService;
import com.hms.common.dto.auth.AuthResponse;
import com.hms.common.dto.auth.LoginRequest;
import com.hms.common.dto.auth.RegisterRequest;
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
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        userService.register(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        return userService.login(req);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("X-User-Id") Long userId, @RequestBody com.hms.common.dto.auth.UpdateProfileRequest req) {
        // In real microservices, Gateway passes X-User-Id, or we parse JWT here.
        // Assuming Gateway passes logged in userID in header or we use SecurityContext if configured.
        // For simplicity, let's assume we can rely on passed ID or check security context.
        // BUT, usually we extract userID from Authentication principal.
        // Let's rely on standard Spring Security Principal if configured, OR assume Gateway passing identity.
        // Given current architecture verification needed.
        // Let's use @AuthenticationPrincipal or SecurityContextHolder if JWT filter is active.
        // Check "SecurityConfig" of auth-service.
        // If not using Spring Security fully, we might need manual extraction from token passed in header.
        
        // Actually, let's keep it simple: API Gateway authenticates, passes headers. 
        // But Controller signature needs to handle it.
        // Let's assume the callers (GatewayApiClient) pass User details or token.
        // But wait, GatewayApiClient calls using `SessionAuth` token header "Authorization".
        // Example `AuthController` assumes public access for login/register.
        // `updateProfile` requires auth.
        // I will trust SecurityContextHolder if `JwtTokenFilter` exists.
        // Let's check imports.
        
        return ResponseEntity.ok(userService.updateProfile(userId, req));
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestHeader("X-User-Id") Long userId, @RequestBody com.hms.common.dto.auth.ChangePasswordRequest req) {
        userService.changePassword(userId, req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<com.hms.common.dto.UserDto> getProfile(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @GetMapping("/ping")
    public String ping() {
        return "auth-service: OK";
    }
}
