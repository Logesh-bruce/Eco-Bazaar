package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.SellerProfile;
import com.example.EcoBazaar_module2.model.User;
import com.example.EcoBazaar_module2.repository.SellerProfileRepository;
import com.example.EcoBazaar_module2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    @Autowired
    private AuditService auditService;

    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateUserProfile(Long userId, String fullName) {
        User user = getUserProfile(userId);
        user.setFullName(fullName);
        return userRepository.save(user);
    }

    @Transactional
    public SellerProfile createSellerProfile(Long userId, String businessName, String description, String contactPhone) {
        User user = getUserProfile(userId);

        if (sellerProfileRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Seller profile already exists");
        }

        SellerProfile profile = new SellerProfile();
        profile.setUser(user);
        profile.setBusinessName(businessName);
        profile.setDescription(description);
        profile.setContactPhone(contactPhone);
        profile.setActive(true);

        return sellerProfileRepository.save(profile);
    }

    public List<User> getAllUsers(Long adminId) {
        auditService.log(adminId, "VIEW_ALL_USERS", "USER", 0L, null);
        return userRepository.findAll();
    }

    @Transactional
    public void deactivateUser(Long adminId, Long userId, String reason) {
        User user = getUserProfile(userId);
        user.setActive(false);
        userRepository.save(user);

        auditService.log(adminId, "DEACTIVATE_USER", "USER", userId, reason);
    }

    @Transactional
    public void activateUser(Long adminId, Long userId) {
        User user = getUserProfile(userId);
        user.setActive(true);
        userRepository.save(user);

        auditService.log(adminId, "ACTIVATE_USER", "USER", userId, null);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }
}