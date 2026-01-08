package com.hms.common.dto.auth;

import java.util.List;

public class AuthResponse {
    private String token;
    private Long userId;
    private List<String> roles;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long userId, List<String> roles) {
        this.token = token;
        this.userId = userId;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
