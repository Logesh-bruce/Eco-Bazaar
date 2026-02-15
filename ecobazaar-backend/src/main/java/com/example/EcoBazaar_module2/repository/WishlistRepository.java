package com.example.EcoBazaar_module2.repository;

import com.example.EcoBazaar_module2.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserId(Long userId);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);
}
