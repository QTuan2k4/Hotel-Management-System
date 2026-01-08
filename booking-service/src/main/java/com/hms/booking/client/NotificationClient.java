package com.hms.booking.client;

import com.hms.booking.config.ClientsProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

        // Internal key might not be needed for this public-ish endpoint, but good
        // practice if secured
        HttpHeaders headers = new HttpHeaders();
        // headers.set("X-Internal-Key", props.getInternalKey());

        HttpEntity<EmailRequest> entity = new HttpEntity<>(req, headers);
        try {
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
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
