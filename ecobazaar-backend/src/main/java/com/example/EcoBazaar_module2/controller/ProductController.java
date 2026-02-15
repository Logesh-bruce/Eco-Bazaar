package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.model.Product;
import com.example.EcoBazaar_module2.model.ProductCarbonData;
import com.example.EcoBazaar_module2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
     *
     * Query Parameters:
     * - search: Product name search
     * - category: Filter by category
     * - minPrice, maxPrice: Price range filter
     * - minCarbon, maxCarbon: Carbon footprint range filter
     * - featured: Filter featured products
     * - sortBy: price_asc, price_desc, carbon_asc, carbon_desc, rating, popular, newest
     * - page, size: Pagination
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
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        productService.incrementProductView(id);
        return ResponseEntity.ok(toProductDTO(product));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Map<String, Object>>> getFeaturedProducts() {
        List<Product> products = productService.getFeaturedProducts();
        return ResponseEntity.ok(products.stream()
                .map(this::toProductDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String name = request.get("name").toString();
            String description = request.get("description").toString();
            Double price = Double.valueOf(request.get("price").toString());
            Integer quantity = Integer.valueOf(request.getOrDefault("quantity", 1).toString());
            String category = request.get("category").toString();
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
            String name = request.get("name").toString();
            String description = request.get("description").toString();
            Double price = Double.valueOf(request.get("price").toString());
            Integer quantity = Integer.valueOf(request.getOrDefault("quantity", 1).toString());
            String category = request.get("category").toString();
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

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Map<String, Object>>> getSellerProducts(@PathVariable Long sellerId) {
        List<Product> products = productService.getSellerProducts(sellerId);
        return ResponseEntity.ok(products.stream()
                .map(this::toProductDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingProducts() {
        List<Product> products = productService.getPendingProducts();
        return ResponseEntity.ok(products.stream()
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

    private Map<String, Object> toProductDTO(Product product) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", product.getId());
        dto.put("name", product.getName());
        dto.put("description", product.getDescription());
        dto.put("price", product.getPrice());
        dto.put("quantity", product.getQuantity());
        dto.put("imageUrl", product.getImageUrl());
        dto.put("category", product.getCategory());
        dto.put("carbonFootprint", product.getTotalCarbonFootprint());
        dto.put("ecoRating", product.getEcoRating());
        dto.put("verified", product.isVerified());
        dto.put("featured", product.isFeatured());
        dto.put("sellerId", product.getSeller().getId());
        dto.put("sellerName", product.getSeller().getFullName());
        dto.put("viewCount", product.getViewCount());
        dto.put("soldCount", product.getSoldCount());
        dto.put("averageRating", product.getAverageRating());
        dto.put("reviewCount", product.getReviewCount());
        dto.put("createdAt", product.getCreatedAt());

        if (product.getCarbonData() != null) {
            Map<String, Double> breakdown = new HashMap<>();
            breakdown.put("manufacturing", product.getCarbonData().getManufacturing());
            breakdown.put("transportation", product.getCarbonData().getTransportation());
            breakdown.put("packaging", product.getCarbonData().getPackaging());
            breakdown.put("usage", product.getCarbonData().getUsage());
            breakdown.put("disposal", product.getCarbonData().getDisposal());
            dto.put("carbonBreakdown", breakdown);
        }

        return dto;
    }
}