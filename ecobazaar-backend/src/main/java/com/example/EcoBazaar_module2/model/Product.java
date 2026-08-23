package com.example.EcoBazaar_module2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 500, columnDefinition = "VARCHAR(500)")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price = 0.0;

    @Column(nullable = false)
    private Integer quantity = 1;

    // Store Base64 encoded image directly in database as TEXT (without @Lob to avoid PostgreSQL OID issues)
    @Column(name = "image_base64", columnDefinition = "TEXT")
    private String imageBase64;

    @Column(name = "category", length = 100, columnDefinition = "VARCHAR(100)")
    private String category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    @JsonIgnoreProperties({"products", "orders", "cart", "password", "hibernateLazyInitializer", "handler"})
    private User seller;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean verified = false;

    // Explicit approval status ("PENDING", "APPROVED", "REJECTED")
    @Column(name = "status", length = 50, columnDefinition = "VARCHAR(50)")
    private String status = "PENDING";

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

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"product", "hibernateLazyInitializer", "handler"})
    private ProductCarbonData carbonData;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"product", "hibernateLazyInitializer", "handler"})
    private List<Review> reviews = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void setVerified(boolean verified) {
        this.verified = verified;
        this.status = verified ? "APPROVED" : "PENDING";
    }

    public void setStatus(String status) {
        this.status = status;
        this.verified = status != null && "APPROVED".equalsIgnoreCase(status.trim());
    }

    // Helper method to get image for display
    @Transient
    public String getImageUrl() {
        if (imageBase64 == null || imageBase64.trim().isEmpty()) {
            return "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300'%3E%3Crect fill='%23f0f0f0' width='300' height='300'/%3E%3Ctext fill='%23999' x='50%25' y='50%25' text-anchor='middle' dy='.3em' font-family='Arial' font-size='18'%3ENo Image%3C/text%3E%3C/svg%3E";
        }
        String trimmed = imageBase64.trim();
        if (trimmed.startsWith("data:image/") || trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("/")) {
            return trimmed;
        }
        return "data:image/jpeg;base64," + trimmed;
    }

    @Transient
    public Double getTotalCarbonFootprint() {
        return (carbonData != null && carbonData.getTotalCO2e() != null) ? carbonData.getTotalCO2e() : 0.0;
    }

    @Transient
    public String getEcoRating() {
        Double total = getTotalCarbonFootprint();
        if (total == null || total < 2.0) return "A+";
        else if (total < 5.0) return "B";
        else return "C";
    }

    public void incrementViewCount() {
        if (this.viewCount == null) this.viewCount = 0;
        this.viewCount++;
    }

    public void incrementSoldCount(int quantity) {
        if (this.soldCount == null) this.soldCount = 0;
        this.soldCount += quantity;
    }

    public void updateRating(double newRating, int newReviewCount) {
        this.averageRating = newRating;
        this.reviewCount = newReviewCount;
    }
}