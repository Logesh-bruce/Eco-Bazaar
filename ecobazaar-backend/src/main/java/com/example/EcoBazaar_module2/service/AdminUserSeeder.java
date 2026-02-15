package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.Role;
import com.example.EcoBazaar_module2.model.User;
import com.example.EcoBazaar_module2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with a default admin user
 * This ensures there's always one admin account in the system
 */
@Component
@Order(1) // Run before CategorySeeder
public class AdminUserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        boolean adminExists = userRepository.findByRole(Role.ADMIN).stream()
                .anyMatch(user -> user.getEmail().equals("admin@ecobazaar.com"));

        if (!adminExists) {
            User admin = new User();
            admin.setEmail("admin@ecobazaar.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Change this password in production!
            admin.setFullName("System Administrator");
            admin.setRole(Role.ADMIN);
            admin.setActive(true);

            userRepository.save(admin);

            System.out.println("=====================================");
            System.out.println("✓ Admin user created successfully!");
            System.out.println("  Email: admin@ecobazaar.com");
            System.out.println("  Password: admin123");
            System.out.println("  IMPORTANT: Change this password in production!");
            System.out.println("=====================================");
        } else {
            System.out.println("✓ Admin user already exists");
        }
    }
}