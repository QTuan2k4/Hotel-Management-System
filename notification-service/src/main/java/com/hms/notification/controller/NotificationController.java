package com.hms.notification.controller;

import com.hms.notification.dto.EmailRequest;
import com.hms.notification.service.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public void sendEmail(@RequestBody EmailRequest req) {
        emailService.sendSimpleMessage(req.getTo(), req.getSubject(), req.getBody());
    }
}
