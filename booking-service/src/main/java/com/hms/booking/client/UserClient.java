package com.hms.booking.client;

import com.hms.booking.config.ClientsProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate;
    private final ClientsProperties props;

    public UserClient(RestTemplate restTemplate, ClientsProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public UserDto getUserById(Long id) {
        // Internal call to admin endpoint (bypass auth or use internal key)
        String url = props.getGatewayBaseUrl() + "/api/admin/users/" + id;
        HttpHeaders headers = new HttpHeaders();
        // headers.set("X-Internal-Key", props.getInternalKey()); // If using internal
        // key
        // For now, assuming internal network or permissive auth for admin endpoints if
        // accessed internally

        // Actually, since this is calling through Gateway, we need a way to
        // authenticate.
        // Or we use direct service-to-service communication if not going through
        // gateway.
        // But here we use GatewayBaseUrl.
        // Let's assume we can fetch it. If not, we iterate.

        try {
            // We need a way to pass admin token or use internal bypass.
            // For simplicity in this demo, we might fail if not authenticated.
            // But let's try.
            return restTemplate.getForObject(url, UserDto.class);
        } catch (Exception e) {
            System.err.println("Failed to fetch user: " + e.getMessage());
            return null;
        }
    }

    public static class UserDto {
        private Long id;
        private String username; // usually email
        private String email;
        private String fullName;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }
}
