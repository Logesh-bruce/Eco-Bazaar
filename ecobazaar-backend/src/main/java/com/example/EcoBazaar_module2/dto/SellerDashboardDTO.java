package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

// Seller Dashboard Response
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerDashboardDTO {
    private SellerStatsDTO stats;
    private List<ProductPerformanceDTO> topProducts;
    private List<RecentOrderDTO> recentOrders;
    private Map<String, Double> salesByCategory;
    private RevenueBreakdownDTO revenueBreakdown;
}
