package com.hms.common.dto.auth;

import java.util.List;

public class AuthResponse {
    private String accessToken;
    private Long userId;
    private List<String> roles;

    public AuthResponse() {}

    public AuthResponse(String accessToken, Long userId, List<String> roles) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.roles = roles;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
