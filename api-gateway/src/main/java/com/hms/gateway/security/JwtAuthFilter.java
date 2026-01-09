package com.hms.gateway.security;

import com.hms.common.security.JwtClaims;
import com.hms.common.security.JwtTokenService;
import com.hms.gateway.config.GatewayProperties;
import com.hms.gateway.config.SecurityJwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String ATTR_USER_CTX = "GATEWAY_USER_CTX";

    private final JwtTokenService jwtTokenService;
    private final GatewayProperties gatewayProperties;

    public JwtAuthFilter(SecurityJwtProperties jwtProps, GatewayProperties gatewayProperties) {
        this.jwtTokenService = new JwtTokenService(jwtProps.getSecret(), jwtProps.getIssuer());
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Only protect /api/**
        if (!path.startsWith("/api/"))
            return true;

        // Public bypasses
        if (path.equals("/api/auth/login") && "POST".equalsIgnoreCase(method))
            return true;
        if (path.equals("/api/auth/register") && "POST".equalsIgnoreCase(method))
            return true;

        if (path.startsWith("/api/rooms/") && "GET".equalsIgnoreCase(method))
            return true;
        if (path.equals("/api/rooms") && "GET".equalsIgnoreCase(method))
            return true;

        // health/ping for skeleton services
        if (path.endsWith("/ping"))
            return true;

        // Payment callback - must be public for redirect from payment gateway
        if (path.startsWith("/api/payments/vnpay/") && "GET".equalsIgnoreCase(method))
            return true;

        // Public uploaded files (images)
        if (path.startsWith("/api/uploads/") && "GET".equalsIgnoreCase(method))
            return true;

        // Booked dates - public endpoint for date picker
        if (path.startsWith("/api/bookings/booked-dates/") && "GET".equalsIgnoreCase(method))
            return true;

        // internal endpoints: protected by internal key (not JWT)
        if (path.startsWith("/api/internal/"))
            return true;

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Internal endpoints: use X-Internal-Key
        // Exception: /api/notifications can also be accessed via JWT (for frontend polling)
        if (path.startsWith("/api/internal/")) {
            String internalKey = request.getHeader("X-Internal-Key");
            String expected = gatewayProperties.getSecurity().getInternalKey();

            if (expected != null && !expected.isBlank() && expected.equals(internalKey)) {
                filterChain.doFilter(request, response);
                return;
            }
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "UNAUTHORIZED", "Missing/invalid X-Internal-Key");
            return;
        }

        // Check for Internal Key first (for Service-to-Service calls like Booking -> Notification)
        if (path.startsWith("/api/notifications")) {
            String internalKey = request.getHeader("X-Internal-Key");
            String expected = gatewayProperties.getSecurity().getInternalKey();
            if (expected != null && !expected.isBlank() && expected.equals(internalKey)) {
                filterChain.doFilter(request, response);
                return;
            }
            // If no internal key, fall through to JWT check below
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "UNAUTHORIZED", "Missing Authorization Bearer token");
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        try {
            Claims claims = jwtTokenService.validateAndGetClaims(token);

            long userId = Long.parseLong(String.valueOf(claims.get(JwtClaims.CLAIM_USER_ID)));
            String username = String.valueOf(claims.get(JwtClaims.CLAIM_USERNAME));
            List<String> roles = JwtTokenService.rolesFromClaims(claims);

            GatewayUserContext ctx = new GatewayUserContext(userId, username, roles);

            // Admin-only area
            if (path.startsWith("/api/admin/") && !ctx.isAdmin()) {
                writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                        "FORBIDDEN", "Admin role required");
                return;
            }

            request.setAttribute(ATTR_USER_CTX, ctx);
            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException ex) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "UNAUTHORIZED", "Invalid token: " + ex.getMessage());
        }
    }

    private void writeJson(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");

        String json = """
                {"error":"%s","message":"%s"}
                """.formatted(escapeJson(error), escapeJson(message));

        response.getWriter().write(json);
    }

    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
