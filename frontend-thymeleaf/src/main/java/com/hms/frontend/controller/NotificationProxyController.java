package com.hms.frontend.controller;

import com.hms.frontend.api.GatewayApiClient;
import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Proxy controller for notification API calls from frontend JavaScript
 */
@RestController
@RequestMapping("/api/notify")
public class NotificationProxyController {

    private final GatewayApiClient api;

    public NotificationProxyController(GatewayApiClient api) {
        this.api = api;
    }

    /**
     * Get admin notifications (proxied from notification-service)
     */
    @GetMapping("/admin")
    public ResponseEntity<?> getAdminNotifications(HttpSession session) {
        SessionAuth auth = getAuth(session);
        if (auth == null || !auth.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
        }
        
        try {
            Object result = api.get("/api/notifications/admin", Object.class, auth);
            return ResponseEntity.ok(result != null ? result : Map.of("notifications", java.util.List.of(), "unreadCount", 0));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("notifications", java.util.List.of(), "unreadCount", 0));
        }
    }

    /**
     * Get user notifications
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserNotifications(HttpSession session) {
        SessionAuth auth = getAuth(session);
        if (auth == null || !auth.isLoggedIn()) {
            return ResponseEntity.status(403).body(Map.of("error", "Login required"));
        }
        
        try {
            Object result = api.get("/api/notifications/user/" + auth.getUserId(), Object.class, auth);
            return ResponseEntity.ok(result != null ? result : Map.of("notifications", java.util.List.of(), "unreadCount", 0));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("notifications", java.util.List.of(), "unreadCount", 0));
        }
    }

    /**
     * Mark notification as read
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, HttpSession session) {
        SessionAuth auth = getAuth(session);
        if (auth == null || !auth.isLoggedIn()) {
            return ResponseEntity.status(403).build();
        }
        
        try {
            api.postForStatus("/api/notifications/" + id + "/read", null, auth);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Mark all admin notifications as read
     */
    @PostMapping("/admin/read-all")
    public ResponseEntity<?> markAllAdminAsRead(HttpSession session) {
        SessionAuth auth = getAuth(session);
        if (auth == null || !auth.isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        try {
            api.postForStatus("/api/notifications/admin/read-all", null, auth);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Mark all user notifications as read
     */
    @PostMapping("/user/read-all")
    public ResponseEntity<?> markAllUserAsRead(HttpSession session) {
        SessionAuth auth = getAuth(session);
        if (auth == null || !auth.isLoggedIn()) {
            return ResponseEntity.status(403).build();
        }
        
        try {
            api.postForStatus("/api/notifications/user/" + auth.getUserId() + "/read-all", null, auth);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.ok().build();
        }
    }

    private SessionAuth getAuth(HttpSession session) {
        Object v = session.getAttribute("AUTH");
        return (v instanceof SessionAuth a) ? a : null;
    }
}

