package com.example.EcoBazaar_module2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

// Admin Dashboard Response
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private com.example.EcoBazaar_module2.dto.PlatformStatsDTO platformStats;
    private List<com.example.EcoBazaar_module2.dto.PendingVerificationDTO> pendingVerifications;
    private List<com.example.EcoBazaar_module2.dto.TopSellerDTO> topSellers;
    private List<com.example.EcoBazaar_module2.dto.RecentActivityDTO> recentActivities;
    private com.example.EcoBazaar_module2.dto.CarbonImpactSummaryDTO carbonImpact;
    private Map<String, Integer> userRoleDistribution;
}
