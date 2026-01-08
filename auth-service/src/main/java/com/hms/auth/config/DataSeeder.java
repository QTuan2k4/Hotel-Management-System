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
        // Seed roles
        Role adminRole = roleRepo.findByName("ADMIN").orElseGet(() -> {
            Role r = Role.builder().name("ADMIN").build();
            return roleRepo.save(r);
        });
        Role userRole = roleRepo.findByName("USER").orElseGet(() -> {
            Role r = Role.builder().name("USER").build();
            return roleRepo.save(r);
        });

        // Seed admin user
        if (!userRepo.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .email("admin@hotel.local")
                    .status("ACTIVE")
                    .build();
            admin.getRoles().add(adminRole);
            admin.getRoles().add(userRole);
            userRepo.save(admin);
            System.out.println("Created default admin user: admin/admin123");
        }
    }
}
