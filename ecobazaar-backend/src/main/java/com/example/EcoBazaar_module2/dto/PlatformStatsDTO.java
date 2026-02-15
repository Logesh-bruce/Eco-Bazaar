package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformStatsDTO {
    private Integer totalUsers;
    private Integer totalSellers;
    private Integer totalProducts;
    private Integer totalOrders;
    private Double totalRevenue;
    private Double platformCarbonFootprint;
    private Double platformCarbonSaved;
    private Integer activeUsers;
}
