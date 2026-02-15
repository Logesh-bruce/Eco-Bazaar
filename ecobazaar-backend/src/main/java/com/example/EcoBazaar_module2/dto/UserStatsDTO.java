package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    private Integer ecoScore;
    private Double carbonSaved;
    private Double totalCarbonFootprint;
    private String badge;
    private Integer greenPurchases;
    private Integer totalOrders;
    private Integer totalItemsPurchased;
    private Double totalSpent;
}
