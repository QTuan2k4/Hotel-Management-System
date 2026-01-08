package com.hms.auth.dto;

import java.util.List;

public class SetRolesRequest {
    private List<String> roles;

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
