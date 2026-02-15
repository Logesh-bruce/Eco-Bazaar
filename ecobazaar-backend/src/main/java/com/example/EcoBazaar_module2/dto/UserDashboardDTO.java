package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

// User Dashboard Response
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardDTO {
    private UserStatsDTO stats;
    private List<RecentOrderDTO> recentOrders;
    private List<AchievementDTO> achievements;
    private List<CarbonTipDTO> personalizedTips;
    private CarbonTrendDTO carbonTrend;
    private Map<String, Double> categoryBreakdown;
    private Map<String, Integer> ecoRatingDistribution;
}