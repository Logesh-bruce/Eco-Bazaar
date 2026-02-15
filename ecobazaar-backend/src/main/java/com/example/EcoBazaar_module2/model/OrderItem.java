package com.example.EcoBazaar_module2.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"seller", "reviews", "carbonData"})
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    // Immutable snapshots at purchase time
    @Column(nullable = false)
    private Double priceSnapshot;

    @Column(nullable = false)
    private Double carbonSnapshot;

    @Column(nullable = false)
    private String productNameSnapshot;
}