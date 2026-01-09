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
        // Use internal endpoint with X-Internal-Key for service-to-service auth
        String url = props.getGatewayBaseUrl() + "/api/internal/users/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Key", props.getInternalKey());

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(url, HttpMethod.GET, entity, UserDto.class).getBody();
        } catch (Exception e) {
            System.err.println("Failed to fetch user: " + e.getMessage());
            return null;
        }
    }

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserDto {
        private Long id;
        private String username;
        private String email;
        private String status;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        // For email, use email field or fallback to username
        public String getEffectiveEmail() {
            if (email != null && !email.isEmpty()) {
                return email;
            }
            return username;
        }
    }
}
