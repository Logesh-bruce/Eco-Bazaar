package com.example.EcoBazaar_module2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_carbon_data")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductCarbonData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"carbonData", "seller", "reviews", "hibernateLazyInitializer", "handler"})
    private Product product;

    @Column(nullable = false)
    private Double manufacturing = 0.0;

    @Column(nullable = false)
    private Double transportation = 0.0;

    @Column(nullable = false)
    private Double packaging = 0.0;

    @Column(nullable = false)
    private Double usage = 0.0;

    @Column(nullable = false)
    private Double disposal = 0.0;

    // Always computed with full null safety to prevent NPE on unboxing
    @Transient
    public Double getTotalCO2e() {
        double m = manufacturing != null ? manufacturing : 0.0;
        double t = transportation != null ? transportation : 0.0;
        double p = packaging != null ? packaging : 0.0;
        double u = usage != null ? usage : 0.0;
        double d = disposal != null ? disposal : 0.0;
        return m + t + p + u + d;
    }
}