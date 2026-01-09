package com.hms.frontend.controller;

import com.hms.frontend.config.FrontendProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Controller
public class FrontendImageProxyController {

    private final RestTemplate restTemplate;
    private final FrontendProperties props;

    public FrontendImageProxyController(RestTemplate restTemplate, FrontendProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    @GetMapping("/api/uploads/**")
    @ResponseBody
    public ResponseEntity<byte[]> proxyImage(HttpServletRequest request) {
        String path = request.getRequestURI();
        String url = props.getGatewayBaseUrl() + path;

        try {
            // Forward request to gateway
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    byte[].class
            );

            // Copy headers and body
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(response.getHeaders());
            
            return ResponseEntity.status(response.getStatusCode())
                    .headers(headers)
                    .body(response.getBody());

        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
