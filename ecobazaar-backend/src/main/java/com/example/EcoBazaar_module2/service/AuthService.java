package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.Role;
import com.example.EcoBazaar_module2.model.User;
import com.example.EcoBazaar_module2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public User registerUser(String email, String password, String fullName, Role role) {
        // Validate email is not already in use
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // IMPORTANT: Restrict role creation - only SELLER and USER can be created via signup
        if (role == Role.ADMIN) {
            throw new RuntimeException("Admin accounts cannot be created through registration. Please contact system administrator.");
        }

        // Only allow SELLER and USER roles
        if (role != Role.SELLER && role != Role.USER) {
            throw new RuntimeException("Invalid role. Only SELLER and USER (Shopper) roles are allowed for registration.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setRole(role);
        user.setActive(true);

        return userRepository.save(user);
    }

    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}