package com.hms.auth.config;

import com.hms.auth.entity.Role;
import com.hms.auth.entity.User;
import com.hms.auth.repository.RoleRepository;
import com.hms.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = roleRepo.findByName("ADMIN").orElseGet(() -> roleRepo.save(Role.builder().name("ADMIN").build()));
        Role userRole = roleRepo.findByName("USER").orElseGet(() -> roleRepo.save(Role.builder().name("USER").build()));

        // default admin user: admin / admin123
        userRepo.findByUsername("admin").orElseGet(() -> {
            User u = User.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .email("admin@local")
                    .status("ACTIVE")
                    .build();
            u.getRoles().add(adminRole);
            u.getRoles().add(userRole);
            return userRepo.save(u);
        });
    }
}
