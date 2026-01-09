package com.hms.booking.client;

import com.hms.booking.config.ClientsProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationClient {

    private final RestTemplate restTemplate;
    private final ClientsProperties props;

    public NotificationClient(RestTemplate restTemplate, ClientsProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public void sendEmail(String to, String subject, String body) {
        String url = props.getGatewayBaseUrl() + "/api/notifications/email";
        EmailRequest req = new EmailRequest(to, subject, body);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Key", props.getInternalKey());

        HttpEntity<EmailRequest> entity = new HttpEntity<>(req, headers);
        try {
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }
    }

    /**
     * Send in-app notification to admin
     */
    public void notifyAdmin(String type, String title, String message, Long referenceId) {
        sendInAppNotification("ADMIN", null, type, title, message, referenceId);
    }

    /**
     * Send in-app notification to specific user
     */
    public void notifyUser(Long userId, String type, String title, String message, Long referenceId) {
        sendInAppNotification(null, userId, type, title, message, referenceId);
    }

    private void sendInAppNotification(String recipientRole, Long recipientUserId,
            String type, String title, String message, Long referenceId) {
        String url = props.getGatewayBaseUrl() + "/api/notifications";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Key", props.getInternalKey());
        headers.set("Content-Type", "application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("recipientRole", recipientRole);
        body.put("recipientUserId", recipientUserId);
        body.put("type", type);
        body.put("title", title);
        body.put("message", message);
        body.put("referenceId", referenceId);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send in-app notification: " + e.getMessage());
        }
    }

    public static class EmailRequest {
        public String to;
        public String subject;
        public String body;

        public EmailRequest(String to, String subject, String body) {
            this.to = to;
            this.subject = subject;
            this.body = body;
        }
    }
}
