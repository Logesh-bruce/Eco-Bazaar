package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.Product;
import com.example.EcoBazaar_module2.model.User;
import com.example.EcoBazaar_module2.model.Wishlist;
import com.example.EcoBazaar_module2.repository.ProductRepository;
import com.example.EcoBazaar_module2.repository.UserRepository;
import com.example.EcoBazaar_module2.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Wishlist> getUserWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    @Transactional
    public Wishlist addToWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if already in wishlist
        if (wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new RuntimeException("Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        Wishlist item = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Item not in wishlist"));
        wishlistRepository.delete(item);
    }

    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }
}
