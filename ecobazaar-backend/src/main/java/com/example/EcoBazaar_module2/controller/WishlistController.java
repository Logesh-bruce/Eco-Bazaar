package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.model.Wishlist;
import com.example.EcoBazaar_module2.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserWishlist(@PathVariable Long userId) {
        List<Wishlist> wishlist = wishlistService.getUserWishlist(userId);
        return ResponseEntity.ok(wishlist.stream()
                .map(item -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", item.getId());
                    dto.put("productId", item.getProduct().getId());
                    dto.put("productName", item.getProduct().getName());
                    dto.put("description", item.getProduct().getDescription());
                    dto.put("price", item.getProduct().getPrice());
                    dto.put("quantity", item.getProduct().getQuantity());
                    // Changed from getPrimaryImage() to getImageUrl()
                    dto.put("image", item.getProduct().getImageUrl());
                    dto.put("category", item.getProduct().getCategory());
                    dto.put("carbonFootprint", item.getProduct().getTotalCarbonFootprint());
                    dto.put("ecoRating", item.getProduct().getEcoRating());
                    dto.put("averageRating", item.getProduct().getAverageRating());
                    dto.put("reviewCount", item.getProduct().getReviewCount());
                    dto.put("addedAt", item.getAddedAt());
                    return dto;
                })
                .toList());
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> addToWishlist(@PathVariable Long userId, @RequestBody Map<String, Long> request) {
        try {
            Long productId = request.get("productId");
            Wishlist item = wishlistService.addToWishlist(userId, productId);
            return ResponseEntity.ok(Map.of("message", "Added to wishlist", "id", item.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long userId, @PathVariable Long productId) {
        try {
            wishlistService.removeFromWishlist(userId, productId);
            return ResponseEntity.ok(Map.of("message", "Removed from wishlist"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkWishlist(@PathVariable Long userId, @PathVariable Long productId) {
        boolean inWishlist = wishlistService.isInWishlist(userId, productId);
        return ResponseEntity.ok(Map.of("inWishlist", inWishlist));
    }
}