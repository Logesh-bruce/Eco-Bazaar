package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPerformanceDTO {
    private Long productId;
    private String productName;
    private Integer unitsSold;
    private Double revenue;
    private Double carbonFootprint;
    private String ecoRating;
}
