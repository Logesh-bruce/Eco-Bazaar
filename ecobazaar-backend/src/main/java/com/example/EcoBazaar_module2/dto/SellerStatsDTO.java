package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerStatsDTO {
    private Integer totalProducts;
    private Integer activeProducts;
    private Integer pendingProducts;
    private Integer totalSales;
    private Double totalRevenue;
    private Double averageCarbonPerProduct;
    private Integer verifiedProducts;
}
