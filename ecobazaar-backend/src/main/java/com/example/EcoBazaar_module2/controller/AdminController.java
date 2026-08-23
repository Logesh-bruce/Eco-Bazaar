package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.model.Order;
import com.example.EcoBazaar_module2.model.Product;
import com.example.EcoBazaar_module2.repository.OrderRepository;
import com.example.EcoBazaar_module2.repository.ProductRepository;
import com.example.EcoBazaar_module2.repository.UserRepository;
import com.example.EcoBazaar_module2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    /**
     * GET /api/admin/stats
     * Returns platform metrics: totalUsers, totalProducts, totalOrders, totalCarbonSaved, pendingApprovals
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            long totalUsers = userRepository.count();
            long totalProducts = productRepository.count();
            long totalOrders = orderRepository.count();

            List<Order> orders = orderRepository.findAll();
            double totalCarbonSaved = 0.0;
            if (orders != null) {
                for (Order order : orders) {
                    if (order != null && order.getTotalCarbonFootprint() != null) {
                        totalCarbonSaved += order.getTotalCarbonFootprint();
                    }
                }
            }

            List<Product> pendingProducts = productService.getPendingProducts();
            long pendingCount = (pendingProducts != null) ? pendingProducts.size() : 0L;

            stats.put("totalUsers", totalUsers);
            stats.put("totalProducts", totalProducts);
            stats.put("totalOrders", totalOrders);
            stats.put("totalCarbonSaved", totalCarbonSaved);
            stats.put("pendingApprovals", pendingCount);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            stats.put("totalUsers", 0L);
            stats.put("totalProducts", 0L);
            stats.put("totalOrders", 0L);
            stats.put("totalCarbonSaved", 0.0);
            stats.put("pendingApprovals", 0L);
            return ResponseEntity.ok(stats);
        }
    }

    /**
     * GET /api/admin/products/pending or /api/admin/pending
     * Returns all unapproved products
     */
    @GetMapping(value = {"/products/pending", "/pending"})
    public ResponseEntity<List<Map<String, Object>>> getPendingProducts() {
        try {
            List<Product> products = productService.getPendingProducts();
            if (products == null || products.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(products.stream()
                    .filter(Objects::nonNull)
                    .map(this::toProductDTO)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * PUT /api/admin/products/{id}/approve or /verify
     * Approves a product and sets status to APPROVED
     */
    @PutMapping(value = {"/products/{id}/approve", "/products/{id}/verify", "/verify/{id}", "/{id}/approve", "/{id}/verify"})
    public ResponseEntity<?> approveProduct(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Long adminId) {
        try {
            Product product = productService.verifyProduct(adminId, id);
            return ResponseEntity.ok(Map.of(
                    "message", "Product approved and live!",
                    "product", toProductDTO(product)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> toProductDTO(Product product) {
        if (product == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", product.getId());
        dto.put("name", product.getName() != null ? product.getName() : "");
        dto.put("description", product.getDescription() != null ? product.getDescription() : "");
        dto.put("price", product.getPrice() != null ? product.getPrice() : 0.0);

        int qty = product.getQuantity() != null ? product.getQuantity() : 0;
        dto.put("quantity", qty);
        dto.put("stock", qty);

        String placeholder = "https://placehold.co/300x300?text=Eco+Product";
        String imgUrl = (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) 
                ? product.getImageUrl().trim() 
                : placeholder;
        String imgBase64 = (product.getImageBase64() != null && !product.getImageBase64().trim().isEmpty()) 
                ? product.getImageBase64().trim() 
                : imgUrl;

        dto.put("imageUrl", imgUrl);
        dto.put("image", imgUrl);
        dto.put("imageBase64", imgBase64);

        String category = product.getCategory() != null ? product.getCategory() : "";
        dto.put("category", category);
        dto.put("categoryName", category);

        Double carbon = product.getTotalCarbonFootprint() != null ? product.getTotalCarbonFootprint() : 0.0;
        dto.put("carbonFootprint", carbon);
        dto.put("carbonScore", carbon);

        dto.put("ecoRating", product.getEcoRating() != null ? product.getEcoRating() : "A+");

        String status = product.getStatus() != null ? product.getStatus() : (product.isVerified() ? "APPROVED" : "PENDING");
        dto.put("status", status);
        dto.put("verified", product.isVerified());
        dto.put("isApproved", product.isVerified() || "APPROVED".equalsIgnoreCase(status));
        dto.put("featured", product.isFeatured());
        dto.put("active", product.isActive());

        if (product.getSeller() != null) {
            dto.put("sellerId", product.getSeller().getId());
            dto.put("sellerName", product.getSeller().getFullName() != null ? product.getSeller().getFullName() : "Seller");
        } else {
            dto.put("sellerId", null);
            dto.put("sellerName", "Seller");
        }

        dto.put("viewCount", product.getViewCount() != null ? product.getViewCount() : 0);
        dto.put("soldCount", product.getSoldCount() != null ? product.getSoldCount() : 0);
        dto.put("averageRating", product.getAverageRating() != null ? product.getAverageRating() : 0.0);
        dto.put("reviewCount", product.getReviewCount() != null ? product.getReviewCount() : 0);
        dto.put("createdAt", product.getCreatedAt());

        return dto;
    }
}
