package com.hms.room.security;

import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

public final class GatewayHeaders {
    private GatewayHeaders() {}

    public static void requireAdmin(HttpHeaders headers) {
        String roles = headers.getFirst("X-Roles");
        if (roles == null || roles.isBlank() || !roles.toUpperCase().contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        }
    }
}
