package com.hms.frontend.session;

import java.io.Serializable;
import java.util.List;

public class SessionAuth implements Serializable {
    private String token;
    private Long userId;
    private String username;
    private List<String> roles;

    public boolean isLoggedIn() { return token != null && !token.isBlank(); }
    public boolean isAdmin() { return roles != null && roles.stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r)); }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

}
