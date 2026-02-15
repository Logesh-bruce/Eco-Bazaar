package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.dto.*;
import com.example.EcoBazaar_module2.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // ============= USER DASHBOARD ENDPOINTS =============

    /**
     * USER DASHBOARD - COMPREHENSIVE
     * GET /api/dashboard/user/{userId}
     *
     * Returns comprehensive dashboard for USER role including:
     * - Stats (eco score, carbon saved, badges)
     * - Recent orders
     * - Achievements
     * - Personalized carbon tips
     * - Carbon trend over time
     * - Category breakdown
     * - Eco rating distribution
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDashboardDTO> getUserDashboard(@PathVariable Long userId) {
        try {
            UserDashboardDTO dashboard = dashboardService.getUserDashboard(userId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * USER STATS ONLY
     * GET /api/dashboard/user/{userId}/stats
     *
     * Returns only user statistics:
     * - Total orders, carbon footprint, spent
     * - Green purchases, carbon saved
     * - Eco score and badge
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<UserStatsDTO> getUserStats(@PathVariable Long userId) {
        try {
            // Note: You might need to add a separate method in service for this
            UserDashboardDTO dashboard = dashboardService.getUserDashboard(userId);
            return ResponseEntity.ok(dashboard.getStats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * USER ACHIEVEMENTS
     * GET /api/dashboard/user/{userId}/achievements
     *
     * Returns user achievements with progress tracking
     */
    @GetMapping("/user/{userId}/achievements")
    public ResponseEntity<List<AchievementDTO>> getUserAchievements(@PathVariable Long userId) {
        try {
            UserDashboardDTO dashboard = dashboardService.getUserDashboard(userId);
            return ResponseEntity.ok(dashboard.getAchievements());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PERSONALIZED CARBON TIPS
     * GET /api/dashboard/user/{userId}/tips
     *
     * Returns personalized carbon reduction tips based on user's shopping behavior
     */
    @GetMapping("/user/{userId}/tips")
    public ResponseEntity<List<CarbonTipDTO>> getUserCarbonTips(@PathVariable Long userId) {
        try {
            UserDashboardDTO dashboard = dashboardService.getUserDashboard(userId);
            return ResponseEntity.ok(dashboard.getPersonalizedTips());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * CARBON TREND DATA
     * GET /api/dashboard/user/{userId}/carbon-trend
     *
     * Returns carbon footprint trend over last 12 months
     */
    @GetMapping("/user/{userId}/carbon-trend")
    public ResponseEntity<CarbonTrendDTO> getUserCarbonTrend(@PathVariable Long userId) {
        try {
            UserDashboardDTO dashboard = dashboardService.getUserDashboard(userId);
            return ResponseEntity.ok(dashboard.getCarbonTrend());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * CATEGORY BREAKDOWN
     * GET /api/dashboard/user/{userId}/category-breakdown
     *
     * Returns carbon footprint breakdown by product category
     */
    @GetMapping("/user/{userId}/category-breakdown")
    public ResponseEntity<Map<String, Double>> getUserCategoryBreakdown(@PathVariable Long userId) {
        try {
            UserDashboardDTO dashboard = dashboardService.getUserDashboard(userId);
            return ResponseEntity.ok(dashboard.getCategoryBreakdown());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * ECO RATING DISTRIBUTION
     * GET /api/dashboard/user/{userId}/eco-rating-distribution
     *
     * Returns distribution of purchased products by eco-rating (A+, A, B, C, D)
     */
    @GetMapping("/user/{userId}/eco-rating-distribution")
    public ResponseEntity<Map<String, Integer>> getUserEcoRatingDistribution(@PathVariable Long userId) {
        try {
            UserDashboardDTO dashboard = dashboardService.getUserDashboard(userId);
            return ResponseEntity.ok(dashboard.getEcoRatingDistribution());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * RECENT ORDERS
     * GET /api/dashboard/user/{userId}/recent-orders
     * Optional query param: limit (default: 10)
     *
     * Returns recent orders with details
     */
    @GetMapping("/user/{userId}/recent-orders")
    public ResponseEntity<List<RecentOrderDTO>> getUserRecentOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            UserDashboardDTO dashboard = dashboardService.getUserDashboard(userId);
            return ResponseEntity.ok(dashboard.getRecentOrders().stream()
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ============= SELLER DASHBOARD ENDPOINTS =============

    /**
     * SELLER DASHBOARD - COMPREHENSIVE
     * GET /api/dashboard/seller/{sellerId}
     *
     * Returns comprehensive dashboard for SELLER role including:
     * - Stats (total products, sales, revenue)
     * - Top performing products
     * - Recent orders
     * - Sales by category
     * - Revenue breakdown (today/week/month/total)
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<SellerDashboardDTO> getSellerDashboard(@PathVariable Long sellerId) {
        try {
            SellerDashboardDTO dashboard = dashboardService.getSellerDashboard(sellerId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * SELLER STATS
     * GET /api/dashboard/seller/{sellerId}/stats
     *
     * Returns seller statistics
     */
    @GetMapping("/seller/{sellerId}/stats")
    public ResponseEntity<SellerStatsDTO> getSellerStats(@PathVariable Long sellerId) {
        try {
            SellerDashboardDTO dashboard = dashboardService.getSellerDashboard(sellerId);
            return ResponseEntity.ok(dashboard.getStats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * TOP PRODUCTS
     * GET /api/dashboard/seller/{sellerId}/top-products
     * Optional query param: limit (default: 5)
     *
     * Returns top performing products by revenue
     */
    @GetMapping("/seller/{sellerId}/top-products")
    public ResponseEntity<List<ProductPerformanceDTO>> getSellerTopProducts(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            SellerDashboardDTO dashboard = dashboardService.getSellerDashboard(sellerId);
            return ResponseEntity.ok(dashboard.getTopProducts().stream()
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * SALES BY CATEGORY
     * GET /api/dashboard/seller/{sellerId}/sales-by-category
     *
     * Returns sales revenue breakdown by product category
     */
    @GetMapping("/seller/{sellerId}/sales-by-category")
    public ResponseEntity<Map<String, Double>> getSalesByCategory(@PathVariable Long sellerId) {
        try {
            SellerDashboardDTO dashboard = dashboardService.getSellerDashboard(sellerId);
            return ResponseEntity.ok(dashboard.getSalesByCategory());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * REVENUE BREAKDOWN
     * GET /api/dashboard/seller/{sellerId}/revenue-breakdown
     *
     * Returns revenue breakdown: today, week, month, total
     */
    @GetMapping("/seller/{sellerId}/revenue-breakdown")
    public ResponseEntity<RevenueBreakdownDTO> getRevenueBreakdown(@PathVariable Long sellerId) {
        try {
            SellerDashboardDTO dashboard = dashboardService.getSellerDashboard(sellerId);
            return ResponseEntity.ok(dashboard.getRevenueBreakdown());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ============= ADMIN DASHBOARD ENDPOINTS =============

    /**
     * ADMIN DASHBOARD - COMPREHENSIVE
     * GET /api/dashboard/admin
     *
     * Returns comprehensive dashboard for ADMIN role including:
     * - Platform statistics (users, sellers, products, orders)
     * - Pending verifications
     * - Top sellers
     * - Recent audit activities
     * - Platform carbon impact summary
     * - User role distribution
     */
    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardDTO> getAdminDashboard() {
        try {
            AdminDashboardDTO dashboard = dashboardService.getAdminDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PLATFORM STATS
     * GET /api/dashboard/admin/platform-stats
     *
     * Returns platform statistics
     */
    @GetMapping("/admin/platform-stats")
    public ResponseEntity<PlatformStatsDTO> getPlatformStats() {
        try {
            AdminDashboardDTO dashboard = dashboardService.getAdminDashboard();
            return ResponseEntity.ok(dashboard.getPlatformStats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PENDING VERIFICATIONS
     * GET /api/dashboard/admin/pending-verifications
     *
     * Returns list of products pending verification
     */
    @GetMapping("/admin/pending-verifications")
    public ResponseEntity<List<PendingVerificationDTO>> getPendingVerifications() {
        try {
            AdminDashboardDTO dashboard = dashboardService.getAdminDashboard();
            return ResponseEntity.ok(dashboard.getPendingVerifications());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * TOP SELLERS
     * GET /api/dashboard/admin/top-sellers
     * Optional query param: limit (default: 10)
     *
     * Returns top sellers by revenue
     */
    @GetMapping("/admin/top-sellers")
    public ResponseEntity<List<TopSellerDTO>> getTopSellers(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            AdminDashboardDTO dashboard = dashboardService.getAdminDashboard();
            return ResponseEntity.ok(dashboard.getTopSellers().stream()
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * RECENT ACTIVITIES
     * GET /api/dashboard/admin/recent-activities
     * Optional query param: limit (default: 10)
     *
     * Returns recent audit activities
     */
    @GetMapping("/admin/recent-activities")
    public ResponseEntity<List<RecentActivityDTO>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            AdminDashboardDTO dashboard = dashboardService.getAdminDashboard();
            return ResponseEntity.ok(dashboard.getRecentActivities().stream()
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * CARBON IMPACT SUMMARY
     * GET /api/dashboard/admin/carbon-impact
     *
     * Returns platform carbon impact summary
     */
    @GetMapping("/admin/carbon-impact")
    public ResponseEntity<CarbonImpactSummaryDTO> getCarbonImpact() {
        try {
            AdminDashboardDTO dashboard = dashboardService.getAdminDashboard();
            return ResponseEntity.ok(dashboard.getCarbonImpact());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * USER ROLE DISTRIBUTION
     * GET /api/dashboard/admin/user-role-distribution
     *
     * Returns distribution of users by role (USER, SELLER, ADMIN)
     */
    @GetMapping("/admin/user-role-distribution")
    public ResponseEntity<Map<String, Integer>> getUserRoleDistribution() {
        try {
            AdminDashboardDTO dashboard = dashboardService.getAdminDashboard();
            return ResponseEntity.ok(dashboard.getUserRoleDistribution());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ============= ERROR HANDLING =============

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "DASHBOARD_ERROR",
                "Error fetching dashboard data: " + e.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.badRequest().body(error);
    }
}