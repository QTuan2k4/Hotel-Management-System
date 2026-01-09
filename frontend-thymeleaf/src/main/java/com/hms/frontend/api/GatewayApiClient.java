package com.hms.frontend.api;

import com.hms.frontend.config.FrontendProperties;
import com.hms.frontend.session.SessionAuth;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class GatewayApiClient {

    private final RestTemplate restTemplate;
    private final FrontendProperties props;

    public GatewayApiClient(RestTemplate restTemplate, FrontendProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public <T> T get(String path, Class<T> responseType, SessionAuth auth) {
        String url = props.getGatewayBaseUrl() + path;
        HttpHeaders headers = headers(auth);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<T> resp = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            return null;
        }
    }

    /**
     * GET request without authentication (for public endpoints)
     */
    public <T> T getPublic(String path, Class<T> responseType) {
        String url = props.getGatewayBaseUrl() + path;
        try {
            ResponseEntity<T> resp = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, responseType);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            return null;
        }
    }

    public <T, R> T post(String path, R body, Class<T> responseType, SessionAuth auth) {
        String url = props.getGatewayBaseUrl() + path;
        HttpHeaders headers = headers(auth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<R> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<T> resp = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            return null;
        }
    }

    /**
     * POST request that returns true for success (2xx), false for any error.
     * Use this for endpoints that return empty body on success (like /register).
     */
    public <R> boolean postForStatus(String path, R body, SessionAuth auth) {
        String url = props.getGatewayBaseUrl() + path;
        HttpHeaders headers = headers(auth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<R> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Void> resp = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
            return resp.getStatusCode().is2xxSuccessful();
        } catch (HttpStatusCodeException e) {
            return false;
        }
    }

    public <T, R> T put(String path, R body, Class<T> responseType, SessionAuth auth) {
        String url = props.getGatewayBaseUrl() + path;
        HttpHeaders headers = headers(auth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<R> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<T> resp = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            return null;
        }
    }

    public void delete(String path, SessionAuth auth) {
        String url = props.getGatewayBaseUrl() + path;
        HttpHeaders headers = headers(auth);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        } catch (HttpStatusCodeException e) {
            // ignore
        }
    }

    private HttpHeaders headers(SessionAuth auth) {
        HttpHeaders h = new HttpHeaders();
        if (auth != null && auth.getToken() != null) {
            h.set("Authorization", "Bearer " + auth.getToken());
        }
        return h;
    }
}
