package com.example.EcoBazaar_module2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FIX: Changed from TEXT to VARCHAR to avoid PostgreSQL bytea issue
    @Column(name = "name", length = 500, columnDefinition = "VARCHAR(500)")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity = 1;

    // Store Base64 encoded image directly in database
    @Column(columnDefinition = "TEXT")
    private String imageBase64;

    // FIX: Changed from TEXT to VARCHAR for category as well
    @Column(name = "category", length = 100, columnDefinition = "VARCHAR(100)")
    private String category;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    @JsonIgnoreProperties({"products", "orders", "cart", "password"})
    private User seller;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Integer soldCount = 0;

    @Column(nullable = false)
    private Double averageRating = 0.0;

    @Column(nullable = false)
    private Integer reviewCount = 0;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductCarbonData carbonData;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper method to get image for display
    @Transient
    public String getImageUrl() {
        if (imageBase64 == null || imageBase64.isEmpty()) {
            return "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300'%3E%3Crect fill='%23f0f0f0' width='300' height='300'/%3E%3Ctext fill='%23999' x='50%25' y='50%25' text-anchor='middle' dy='.3em' font-family='Arial' font-size='18'%3ENo Image%3C/text%3E%3C/svg%3E";
        }
        // If it already has data URI prefix, return as is
        if (imageBase64.startsWith("data:image")) {
            return imageBase64;
        }
        // Otherwise add the prefix
        return "data:image/jpeg;base64," + imageBase64;
    }

    @Transient
    public Double getTotalCarbonFootprint() {
        return carbonData != null ? carbonData.getTotalCO2e() : 0.0;
    }

    @Transient
    public String getEcoRating() {
        Double total = getTotalCarbonFootprint();
        if (total < 2.0) return "A+";
        else if (total < 5.0) return "B";
        else return "C";
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementSoldCount(int quantity) {
        this.soldCount += quantity;
    }

    public void updateRating(double newRating, int newReviewCount) {
        this.averageRating = newRating;
        this.reviewCount = newReviewCount;
    }
}