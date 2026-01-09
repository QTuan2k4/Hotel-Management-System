package com.hms.auth.controller;

import com.hms.auth.dto.SetRolesRequest;
import com.hms.common.dto.UserDto;
import com.hms.auth.service.UserService;
import com.hms.common.dto.auth.RegisterRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> list() {
        return userService.listUsers();
    }

    @PostMapping
    public UserDto create(@RequestBody RegisterRequest req,
            @RequestParam(defaultValue = "USER") List<String> roles) {
        return userService.createUser(req, roles);
    }

    @PutMapping("/{id}/roles")
    public UserDto setRoles(@PathVariable Long id, @RequestBody SetRolesRequest req) {
        return userService.setRoles(id, req);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }
}
