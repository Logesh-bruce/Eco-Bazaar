package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.model.Cart;
import com.example.EcoBazaar_module2.model.CartItem;
import com.example.EcoBazaar_module2.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Get user's cart
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getCart(@PathVariable Long userId) {
        Cart cart = cartService.getUserCart(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("id", cart.getId());
        response.put("items", cart.getItems().stream()
                .map(this::mapToCartItemDTO)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    /**
     * Get user's cart with filtering and sorting
     *
     * Query Parameters:
     * - category: Filter by product category
     * - minPrice, maxPrice: Price range filter
     * - minCarbon, maxCarbon: Carbon footprint range filter
     * - sortBy: price_asc, price_desc, carbon_asc, carbon_desc, name_asc, name_desc
     */
    @GetMapping("/{userId}/filtered")
    public ResponseEntity<Map<String, Object>> getFilteredCart(
            @PathVariable Long userId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minCarbon,
            @RequestParam(required = false) Double maxCarbon,
            @RequestParam(required = false) String sortBy
    ) {
        Cart cart = cartService.getUserCart(userId);
        List<CartItem> items = cart.getItems();

        // Apply filters
        List<CartItem> filteredItems = items.stream()
                .filter(item -> {
                    var product = item.getProduct();

                    // Category filter
                    if (category != null && !category.isEmpty() && !category.equals("All")) {
                        if (!product.getCategory().equals(category)) return false;
                    }

                    // Price filter
                    if (minPrice != null && product.getPrice() < minPrice) return false;
                    if (maxPrice != null && product.getPrice() > maxPrice) return false;

                    // Carbon filter
                    double carbon = product.getTotalCarbonFootprint();
                    if (minCarbon != null && carbon < minCarbon) return false;
                    if (maxCarbon != null && carbon > maxCarbon) return false;

                    return true;
                })
                .collect(Collectors.toList());

        // Apply sorting
        if (sortBy != null) {
            filteredItems = applySorting(filteredItems, sortBy);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", cart.getId());
        response.put("items", filteredItems.stream()
                .map(this::mapToCartItemDTO)
                .collect(Collectors.toList()));
        response.put("totalItems", filteredItems.size());
        response.put("totalAmount", calculateTotalAmount(filteredItems));
        response.put("totalCarbon", calculateTotalCarbon(filteredItems));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<?> addItem(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long productId = Long.parseLong(request.get("productId").toString());
            Integer quantity = Integer.parseInt(
                    request.getOrDefault("quantity", 1).toString()
            );

            CartItem item = cartService.addItemToCart(userId, productId, quantity);

            return ResponseEntity.ok(Map.of(
                    "message", "Item added to cart",
                    "itemId", item.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<?> removeItem(
            @PathVariable Long userId,
            @PathVariable Long itemId
    ) {
        try {
            cartService.removeItemFromCart(userId, itemId);

            return ResponseEntity.ok(Map.of(
                    "message", "Item removed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    private List<CartItem> applySorting(List<CartItem> items, String sortBy) {
        Comparator<CartItem> comparator = null;

        switch (sortBy) {
            case "price_asc":
                comparator = Comparator.comparing(item -> item.getProduct().getPrice());
                break;
            case "price_desc":
                comparator = Comparator.comparing((CartItem item) -> item.getProduct().getPrice()).reversed();
                break;
            case "carbon_asc":
                comparator = Comparator.comparing(item -> item.getProduct().getTotalCarbonFootprint());
                break;
            case "carbon_desc":
                comparator = Comparator.comparing((CartItem item) -> item.getProduct().getTotalCarbonFootprint()).reversed();
                break;
            case "name_asc":
                comparator = Comparator.comparing(item -> item.getProduct().getName());
                break;
            case "name_desc":
                comparator = Comparator.comparing((CartItem item) -> item.getProduct().getName()).reversed();
                break;
            default:
                return items;
        }

        return items.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private double calculateTotalAmount(List<CartItem> items) {
        return items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    private double calculateTotalCarbon(List<CartItem> items) {
        return items.stream()
                .mapToDouble(item -> item.getProduct().getTotalCarbonFootprint() * item.getQuantity())
                .sum();
    }

    private Map<String, Object> mapToCartItemDTO(CartItem item) {
        Map<String, Object> dto = new HashMap<>();

        var product = item.getProduct();

        dto.put("id", item.getId());
        dto.put("productId", product.getId());
        dto.put("productName", product.getName());
        dto.put("price", product.getPrice());
        dto.put("quantity", item.getQuantity());
        dto.put("imageUrl", product.getImageUrl());
        dto.put("category", product.getCategory());
        dto.put("carbonFootprint", product.getTotalCarbonFootprint());
        dto.put("ecoRating", product.getEcoRating());

        return dto;
    }
}