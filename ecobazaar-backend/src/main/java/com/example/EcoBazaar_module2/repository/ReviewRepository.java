package com.example.EcoBazaar_module2.repository;

import com.example.EcoBazaar_module2.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);

    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);
}
