package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.model.Product;
import com.example.EcoBazaar_module2.model.ProductCarbonData;
import com.example.EcoBazaar_module2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Enhanced search with comprehensive filtering and sorting
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minCarbon,
            @RequestParam(required = false) Double maxCarbon,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<Product> productPage = productService.searchProductsEnhanced(
                search, category, minPrice, maxPrice, minCarbon, maxCarbon, featured, sortBy, page, size
        );

        Map<String, Object> response = new HashMap<>();
        response.put("products", productPage.getContent().stream()
                .filter(Objects::nonNull)
                .map(this::toProductDTO)
                .collect(Collectors.toList()));
        response.put("currentPage", productPage.getNumber());
        response.put("totalPages", productPage.getTotalPages());
        response.put("totalItems", productPage.getTotalElements());
        response.put("hasNext", productPage.hasNext());
        response.put("hasPrevious", productPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Product not found"));
            }
            productService.incrementProductView(id);
            return ResponseEntity.ok(toProductDTO(product));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Map<String, Object>>> getFeaturedProducts() {
        List<Product> products = productService.getFeaturedProducts();
        if (products == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(products.stream()
                .filter(Objects::nonNull)
                .map(this::toProductDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getPrincipal() == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }
            Long userId = (Long) authentication.getPrincipal();
            String name = request.get("name") != null ? request.get("name").toString() : "";
            String description = request.get("description") != null ? request.get("description").toString() : "";
            Double price = request.get("price") != null ? Double.valueOf(request.get("price").toString()) : 0.0;
            Integer quantity = Integer.valueOf(request.getOrDefault("quantity", 1).toString());
            String category = request.get("category") != null ? request.get("category").toString() : "";
            String imageBase64 = request.getOrDefault("imageBase64", "").toString();

            ProductCarbonData carbonData = new ProductCarbonData();
            carbonData.setManufacturing(Double.valueOf(request.getOrDefault("manufacturing", 0.0).toString()));
            carbonData.setTransportation(Double.valueOf(request.getOrDefault("transportation", 0.0).toString()));
            carbonData.setPackaging(Double.valueOf(request.getOrDefault("packaging", 0.0).toString()));
            carbonData.setUsage(Double.valueOf(request.getOrDefault("usage", 0.0).toString()));
            carbonData.setDisposal(Double.valueOf(request.getOrDefault("disposal", 0.0).toString()));

            Product product = productService.createProduct(userId, name, description, price,
                    quantity, category, imageBase64, carbonData);

            return ResponseEntity.ok(toProductDTO(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String name = request.get("name") != null ? request.get("name").toString() : "";
            String description = request.get("description") != null ? request.get("description").toString() : "";
            Double price = request.get("price") != null ? Double.valueOf(request.get("price").toString()) : 0.0;
            Integer quantity = Integer.valueOf(request.getOrDefault("quantity", 1).toString());
            String category = request.get("category") != null ? request.get("category").toString() : "";
            String imageBase64 = request.getOrDefault("imageBase64", "").toString();

            ProductCarbonData carbonData = new ProductCarbonData();
            carbonData.setManufacturing(Double.valueOf(request.getOrDefault("manufacturing", 0.0).toString()));
            carbonData.setTransportation(Double.valueOf(request.getOrDefault("transportation", 0.0).toString()));
            carbonData.setPackaging(Double.valueOf(request.getOrDefault("packaging", 0.0).toString()));
            carbonData.setUsage(Double.valueOf(request.getOrDefault("usage", 0.0).toString()));
            carbonData.setDisposal(Double.valueOf(request.getOrDefault("disposal", 0.0).toString()));

            Product product = productService.updateProduct(userId, id, name, description,
                    price, quantity, category, imageBase64, carbonData);

            return ResponseEntity.ok(toProductDTO(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, @RequestParam Long userId) {
        try {
            productService.deleteProduct(userId, id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/feature")
    public ResponseEntity<?> toggleFeatured(@PathVariable Long id, @RequestParam Long adminId) {
        try {
            productService.toggleFeatured(adminId, id);
            return ResponseEntity.ok(Map.of("message", "Product featured status updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Fetch products for a specific seller with full null safety and exception handling
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getSellerProducts(@PathVariable Long sellerId) {
        try {
            if (sellerId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Seller ID is required"));
            }
            List<Product> products = productService.getSellerProducts(sellerId);
            if (products == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            List<Map<String, Object>> response = products.stream()
                    .filter(Objects::nonNull)
                    .map(this::toProductDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve seller products: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingProducts() {
        List<Product> products = productService.getPendingProducts();
        if (products == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(products.stream()
                .filter(Objects::nonNull)
                .map(this::toProductDTO)
                .collect(Collectors.toList()));
    }

    @PutMapping("/admin/verify/{id}")
    public ResponseEntity<?> verifyProduct(@PathVariable Long id, @RequestParam Long adminId) {
        try {
            productService.verifyProduct(adminId, id);
            return ResponseEntity.ok(Map.of("message", "Product verified"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Map entity to clean DTO map with comprehensive null-safety checks
     */
    private Map<String, Object> toProductDTO(Product product) {
        if (product == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", product.getId());
        dto.put("name", product.getName() != null ? product.getName() : "");
        dto.put("description", product.getDescription() != null ? product.getDescription() : "");
        dto.put("price", product.getPrice() != null ? product.getPrice() : 0.0);
        dto.put("quantity", product.getQuantity() != null ? product.getQuantity() : 0);
        dto.put("imageUrl", product.getImageUrl() != null ? product.getImageUrl() : "");
        dto.put("category", product.getCategory() != null ? product.getCategory() : "");
        dto.put("carbonFootprint", product.getTotalCarbonFootprint() != null ? product.getTotalCarbonFootprint() : 0.0);
        dto.put("ecoRating", product.getEcoRating() != null ? product.getEcoRating() : "A+");
        dto.put("verified", product.isVerified());
        dto.put("featured", product.isFeatured());

        // Safe seller null checks
        if (product.getSeller() != null) {
            dto.put("sellerId", product.getSeller().getId());
            dto.put("sellerName", product.getSeller().getFullName() != null ? product.getSeller().getFullName() : "Seller");
        } else {
            dto.put("sellerId", null);
            dto.put("sellerName", "Unknown Seller");
        }

        dto.put("viewCount", product.getViewCount() != null ? product.getViewCount() : 0);
        dto.put("soldCount", product.getSoldCount() != null ? product.getSoldCount() : 0);
        dto.put("averageRating", product.getAverageRating() != null ? product.getAverageRating() : 0.0);
        dto.put("reviewCount", product.getReviewCount() != null ? product.getReviewCount() : 0);
        dto.put("createdAt", product.getCreatedAt());

        if (product.getCarbonData() != null) {
            Map<String, Double> breakdown = new HashMap<>();
            breakdown.put("manufacturing", product.getCarbonData().getManufacturing() != null ? product.getCarbonData().getManufacturing() : 0.0);
            breakdown.put("transportation", product.getCarbonData().getTransportation() != null ? product.getCarbonData().getTransportation() : 0.0);
            breakdown.put("packaging", product.getCarbonData().getPackaging() != null ? product.getCarbonData().getPackaging() : 0.0);
            breakdown.put("usage", product.getCarbonData().getUsage() != null ? product.getCarbonData().getUsage() : 0.0);
            breakdown.put("disposal", product.getCarbonData().getDisposal() != null ? product.getCarbonData().getDisposal() : 0.0);
            dto.put("carbonBreakdown", breakdown);
        }

        return dto;
    }
}