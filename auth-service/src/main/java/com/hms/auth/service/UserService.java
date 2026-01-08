package com.hms.auth.service;

import com.hms.auth.config.SecurityJwtProperties;
import com.hms.auth.dto.SetRolesRequest;
import com.hms.auth.dto.UserDto;
import com.hms.auth.entity.Role;
import com.hms.auth.entity.User;
import com.hms.auth.repository.RoleRepository;
import com.hms.auth.repository.UserRepository;
import com.hms.common.dto.auth.AuthResponse;
import com.hms.common.dto.auth.LoginRequest;
import com.hms.common.dto.auth.RegisterRequest;
import com.hms.common.security.JwtTokenService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final SecurityJwtProperties jwtProps;

    public UserService(UserRepository userRepo, RoleRepository roleRepo,
            PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService,
            SecurityJwtProperties jwtProps) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtProps = jwtProps;
    }

    @Transactional
    public void register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        Role userRole = roleRepo.findByName("USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role USER missing"));

        User user = User.builder()
                .username(req.getUsername())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .status("ACTIVE")
                .build();
        user.getRoles().add(userRole);
        userRepo.save(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User disabled");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        List<String> roles = user.getRoles().stream().map(Role::getName).sorted().toList();
        String token = jwtTokenService.generateToken(user.getId(), user.getUsername(), roles, jwtProps.getTtlSeconds());
        return new AuthResponse(token, user.getId(), roles);
    }

    public List<UserDto> listUsers() {
        return userRepo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public UserDto createUser(RegisterRequest req, List<String> roles) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        User user = User.builder()
                .username(req.getUsername())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .status("ACTIVE")
                .build();

        Set<Role> roleSet = roles.stream()
                .map(r -> roleRepo.findByName(r)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + r)))
                .collect(Collectors.toSet());
        user.setRoles(roleSet);
        userRepo.save(user);
        return toDto(user);
    }

    @Transactional
    public UserDto setRoles(Long id, SetRolesRequest req) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Set<Role> roleSet = req.getRoles().stream()
                .map(r -> roleRepo.findByName(r)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + r)))
                .collect(Collectors.toSet());
        user.setRoles(roleSet);
        return toDto(user);
    }

    public UserDto getById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toDto(user);
    }

    private UserDto toDto(User u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setStatus(u.getStatus());
        dto.setRoles(u.getRoles().stream().map(Role::getName).sorted().toList());
        return dto;
    }
}
