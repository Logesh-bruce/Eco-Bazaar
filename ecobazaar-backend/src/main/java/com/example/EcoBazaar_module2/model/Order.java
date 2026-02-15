package com.example.EcoBazaar_module2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"orders", "password", "cart"})
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private Double totalCarbonFootprint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // --- NEW SIMPLE FIELDS ---
    @Column(nullable = false)
    private String shippingAddress; // Just a simple string: "123 Main St, New York"

    @Column(nullable = false)
    private String phoneNumber;     // Contact number for this specific order

    @Column(nullable = false)
    private String paymentMethod;   // "Credit Card", "COD", etc.

    @Column(nullable = false)
    private String paymentStatus;   // "PAID", "PENDING"
    // -------------------------

    @CreationTimestamp
    private LocalDateTime createdAt;
}