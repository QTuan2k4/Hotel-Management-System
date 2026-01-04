package com.hms.gateway.security;

import java.util.List;

public class GatewayUserContext {
    private final long userId;
    private final String username;
    private final List<String> roles;

    public GatewayUserContext(long userId, String username, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }

    public long getUserId() { return userId; }
    public String getUsername() { return username; }
    public List<String> getRoles() { return roles; }

    public boolean isAdmin() {
        return roles != null && roles.stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r));
    }
}
