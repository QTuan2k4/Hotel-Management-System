package com.hms.notification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notify")
public class PingController {
    @GetMapping("/ping")
    public String ping() {
        return "notification-service: OK (skeleton for next phases)";
    }
}
