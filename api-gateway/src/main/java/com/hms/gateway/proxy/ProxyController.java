package com.hms.gateway.proxy;

import com.hms.gateway.security.GatewayUserContext;
import com.hms.gateway.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;

@RestController
public class ProxyController {

    private final RestTemplate restTemplate;
    private final RouteResolver routeResolver;

    public ProxyController(RestTemplate restTemplate, RouteResolver routeResolver) {
        this.restTemplate = restTemplate;
        this.routeResolver = routeResolver;
    }

    @RequestMapping(path = "/api/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) throws IOException {
        String path = request.getRequestURI();
        String baseUrl = routeResolver.resolveBaseUrl(path);

        if (baseUrl == null) {
            String json = """
                    {"error":"BAD_ROUTE","message":"No route for %s"}
                    """.formatted(escapeJson(path));

            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json.getBytes(StandardCharsets.UTF_8));
        }

        String qs = request.getQueryString();
        String targetUrl = baseUrl + path + (qs != null ? "?" + qs : "");

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        byte[] body = StreamUtils.copyToByteArray(request.getInputStream());

        HttpHeaders headers = copyRequestHeaders(request);

        // Add trusted identity headers from gateway (if JWT validated)
        Object attr = request.getAttribute(JwtAuthFilter.ATTR_USER_CTX);
        if (attr instanceof GatewayUserContext ctx) {
            headers.set("X-User-Id", String.valueOf(ctx.getUserId()));
            headers.set("X-Username", ctx.getUsername());
            headers.set("X-Roles", String.join(",", ctx.getRoles()));
        }

        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<byte[]> resp = restTemplate.exchange(targetUrl, method, entity, byte[].class);

            HttpHeaders out = new HttpHeaders();
            resp.getHeaders().forEach((k, v) -> {
                if (!isHopByHopHeader(k))
                    out.put(k, v);
            });

            return ResponseEntity.status(resp.getStatusCode()).headers(out).body(resp.getBody());

        } catch (HttpStatusCodeException ex) {
            HttpHeaders out = new HttpHeaders();
            HttpHeaders exHeaders = ex.getResponseHeaders();
            if (exHeaders != null) {
                exHeaders.forEach((k, v) -> {
                    if (!isHopByHopHeader(k))
                        out.put(k, v);
                });
            }
            return ResponseEntity.status(ex.getStatusCode()).headers(out).body(ex.getResponseBodyAsByteArray());
        }
    }

    private HttpHeaders copyRequestHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null)
            return headers;

        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (isHopByHopHeader(name))
                continue;

            Enumeration<String> values = request.getHeaders(name);
            if (values == null)
                continue;

            for (String v : Collections.list(values)) {
                headers.add(name, v);
            }
        }

        headers.remove(HttpHeaders.HOST);
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        return headers;
    }

    private boolean isHopByHopHeader(String name) {
        if (name == null)
            return false;
        String n = name.toLowerCase();
        return n.equals("host")
                || n.equals("connection")
                || n.equals("keep-alive")
                || n.equals("proxy-authenticate")
                || n.equals("proxy-authorization")
                || n.equals("te")
                || n.equals("trailers")
                || n.equals("transfer-encoding")
                || n.equals("upgrade");
    }

    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
