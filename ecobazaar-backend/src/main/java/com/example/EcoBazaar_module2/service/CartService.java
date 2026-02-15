package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.*;
import com.example.EcoBazaar_module2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Cart getUserCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public CartItem addItemToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getUserCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.isActive() || !product.isVerified()) {
            throw new RuntimeException("Product not available");
        }

        // Check if item already exists
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            return cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = getUserCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getUserCart(userId);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
    }
}