// ReviewController.java
package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.model.Review;
import com.example.EcoBazaar_module2.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long productId = Long.valueOf(request.get("productId").toString());
            Integer rating = Integer.valueOf(request.get("rating").toString());
            String comment = request.getOrDefault("comment", "").toString();

            Review review = reviewService.addReview(userId, productId, rating, comment);
            return ResponseEntity.ok(toReviewDTO(review));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Map<String, Object>>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews.stream()
                .map(this::toReviewDTO)
                .toList());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserReviews(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews.stream()
                .map(this::toReviewDTO)
                .toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id, @RequestParam Long userId) {
        try {
            reviewService.deleteReview(id, userId);
            return ResponseEntity.ok(Map.of("message", "Review deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> toReviewDTO(Review review) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", review.getId());
        dto.put("rating", review.getRating());
        dto.put("comment", review.getComment());
        dto.put("verified", review.isVerified());
        dto.put("userName", review.getUser().getFullName());
        dto.put("userId", review.getUser().getId());
        dto.put("productId", review.getProduct().getId());
        dto.put("productName", review.getProduct().getName());
        dto.put("createdAt", review.getCreatedAt());
        return dto;
    }
}

