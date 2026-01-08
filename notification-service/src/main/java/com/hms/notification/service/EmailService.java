package com.hms.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public EmailService(
            @org.springframework.beans.factory.annotation.Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        if (mailSender == null || fromEmail == null || fromEmail.isEmpty()) {
            // Mock mode: Log to console
            System.out.println("================= MOCK EMAIL =================");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: \n" + text);
            System.out.println("==============================================");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            // Fallback to log
            System.out.println("FAILED EMAIL LOG - To: " + to + ", Subject: " + subject);
        }
    }
}
