package com.hms.notification.controller;

import com.hms.notification.dto.CreateNotificationRequest;
import com.hms.notification.dto.EmailRequest;
import com.hms.notification.dto.NotificationDto;
import com.hms.notification.entity.Notification;
import com.hms.notification.service.EmailService;
import com.hms.notification.service.InAppNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailService emailService;
    private final InAppNotificationService inAppService;

    public NotificationController(EmailService emailService, InAppNotificationService inAppService) {
        this.emailService = emailService;
        this.inAppService = inAppService;
    }

    // ==================== EMAIL ====================

    @PostMapping("/email")
    public void sendEmail(@RequestBody EmailRequest req) {
        emailService.sendSimpleMessage(req.getTo(), req.getSubject(), req.getBody());
    }

    // ==================== IN-APP NOTIFICATIONS ====================

    /**
     * Create a new notification (called by other services)
     */
    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@RequestBody CreateNotificationRequest req) {
        Notification notification;
        
        if ("ADMIN".equals(req.getRecipientRole())) {
            notification = inAppService.notifyAdmin(
                    req.getType(), req.getTitle(), req.getMessage(), req.getReferenceId());
        } else if (req.getRecipientUserId() != null) {
            notification = inAppService.notifyUser(
                    req.getRecipientUserId(), req.getType(), req.getTitle(), req.getMessage(), req.getReferenceId());
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(toDto(notification));
    }

    /**
     * Get admin notifications (for admin dashboard polling)
     */
    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> getAdminNotifications() {
        List<NotificationDto> notifications = inAppService.getAdminNotifications()
                .stream().map(this::toDto).collect(Collectors.toList());
        long unreadCount = inAppService.getUnreadCountAdmin();
        
        return ResponseEntity.ok(Map.of(
                "notifications", notifications,
                "unreadCount", unreadCount
        ));
    }

    /**
     * Get user notifications
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserNotifications(@PathVariable Long userId) {
        List<NotificationDto> notifications = inAppService.getUserNotifications(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
        long unreadCount = inAppService.getUnreadCountUser(userId);
        
        return ResponseEntity.ok(Map.of(
                "notifications", notifications,
                "unreadCount", unreadCount
        ));
    }

    /**
     * Mark a notification as read
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        inAppService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Mark all admin notifications as read
     */
    @PostMapping("/admin/read-all")
    public ResponseEntity<Void> markAllAdminAsRead() {
        inAppService.markAllAdminAsRead();
        return ResponseEntity.ok().build();
    }

    /**
     * Mark all user notifications as read
     */
    @PostMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllUserAsRead(@PathVariable Long userId) {
        inAppService.markAllUserAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // ==================== HELPER ====================

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .referenceId(n.getReferenceId())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
