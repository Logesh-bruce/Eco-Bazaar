// ReviewService.java
package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.*;
import com.example.EcoBazaar_module2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Review addReview(Long userId, Long productId, Integer rating, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if user already reviewed this product
        if (reviewRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new RuntimeException("You have already reviewed this product");
        }

        // Check if user has purchased this product (verified review)
        boolean hasPurchased = orderRepository.findByUserId(userId).stream()
                .anyMatch(order -> order.getItems().stream()
                        .anyMatch(item -> item.getProduct().getId().equals(productId)));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        review.setVerified(hasPurchased);

        Review saved = reviewRepository.save(review);

        // Update product rating
        updateProductRating(productId);

        return saved;
    }

    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        updateProductRating(productId);
    }

    private void updateProductRating(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        if (reviews.isEmpty()) {
            Product product = productRepository.findById(productId).orElseThrow();
            product.updateRating(0.0, 0);
            productRepository.save(product);
            return;
        }

        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        Product product = productRepository.findById(productId).orElseThrow();
        product.updateRating(avgRating, reviews.size());
        productRepository.save(product);
    }
}

