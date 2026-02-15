package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.model.Role;
import com.example.EcoBazaar_module2.model.User;
import com.example.EcoBazaar_module2.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register new user
     * IMPORTANT: Only SELLER and USER roles can be created
     * ADMIN role cannot be created through registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String fullName = request.get("fullName");
            String roleStr = request.get("role");

            // Validate role
            Role role;
            try {
                role = Role.valueOf(roleStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid role. Only SELLER and USER (Shopper) roles are allowed."
                ));
            }

            // The AuthService will validate that only SELLER and USER roles are allowed
            User user = authService.registerUser(email, password, fullName, role);

            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "userId", user.getId(),
                    "role", user.getRole().toString()
            ));
        } catch (RuntimeException e) {
            // Handle role restriction and other errors
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Registration failed: " + e.getMessage()
            ));
        }
    }

    /**
     * Login user
     * Works for all roles including ADMIN
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            User user = authService.authenticateUser(email, password);

            // Return user info
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "fullName", user.getFullName(),
                    "role", user.getRole().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Get allowed roles for registration
     * Returns list of roles that can be created via signup
     */
    @GetMapping("/allowed-roles")
    public ResponseEntity<?> getAllowedRoles() {
        return ResponseEntity.ok(Map.of(
                "allowedRoles", new String[]{"SELLER", "USER"},
                "message", "Only SELLER and USER roles can be created through registration"
        ));
    }
}