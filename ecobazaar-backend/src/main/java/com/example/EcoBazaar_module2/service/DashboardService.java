package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.dto.*;
import com.example.EcoBazaar_module2.model.*;
import com.example.EcoBazaar_module2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // ============= ENHANCED USER DASHBOARD =============
    public UserDashboardDTO getUserDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDashboardDTO dashboard = new UserDashboardDTO();

        List<Order> orders = orderRepository.findByUserId(userId);
        List<Wishlist> wishlistItems = wishlistRepository.findByUserId(userId);
        List<Review> userReviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // Enhanced Stats with more metrics
        dashboard.setStats(calculateEnhancedUserStats(orders, wishlistItems, userReviews));

        // Recent Orders (last 10 instead of 5)
        dashboard.setRecentOrders(getRecentOrders(orders, 10));

        // Enhanced Achievements with progress tracking
        dashboard.setAchievements(calculateEnhancedAchievements(dashboard.getStats(), orders));

        // Personalized Tips based on shopping behavior
        dashboard.setPersonalizedTips(generateEnhancedPersonalizedTips(orders, dashboard.getStats(), wishlistItems));

        // Enhanced Carbon Trend with predictions
        dashboard.setCarbonTrend(calculateEnhancedCarbonTrend(orders));

        // Detailed Category Breakdown with insights
        dashboard.setCategoryBreakdown(calculateDetailedCategoryBreakdown(orders));

        // Eco Rating Distribution with statistics
        dashboard.setEcoRatingDistribution(calculateDetailedEcoRatingDistribution(orders));

        return dashboard;
    }

    private UserStatsDTO calculateEnhancedUserStats(List<Order> orders, List<Wishlist> wishlistItems, List<Review> reviews) {
        UserStatsDTO stats = new UserStatsDTO();

        // Basic order metrics
        int totalOrders = orders.size();
        double totalSpent = orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        int totalItems = orders.stream()
                .mapToInt(o -> o.getItems().size())
                .sum();

        double totalCarbon = orders.stream()
                .mapToDouble(Order::getTotalCarbonFootprint)
                .sum();

        // Enhanced metrics
        int greenPurchases = 0;
        int premiumEcoPurchases = 0; // A+ rated products
        Map<String, Integer> categoryPurchases = new HashMap<>();
        double totalProductValue = 0;
        int repeatedCategories = 0;

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                double itemCarbon = item.getCarbonSnapshot();

                // Count green purchases (< 2kg)
                if (itemCarbon < 2.0) {
                    greenPurchases++;
                }

                // Count premium eco purchases (< 1kg)
                if (itemCarbon < 1.0) {
                    premiumEcoPurchases++;
                }

                // Track category frequency
                String category = item.getProduct().getCategory();
                categoryPurchases.merge(category, 1, Integer::sum);

                totalProductValue += item.getPriceSnapshot() * item.getQuantity();
            }
        }

        // Count categories purchased more than once
        repeatedCategories = (int) categoryPurchases.values().stream()
                .filter(count -> count > 1)
                .count();

        // Calculate carbon metrics
        double baselineCarbon = totalOrders * 10.0; // Assume 10kg per order baseline
        double carbonSaved = Math.max(0, baselineCarbon - totalCarbon);

        // Calculate average carbon per order
        double avgCarbonPerOrder = totalOrders > 0 ? totalCarbon / totalOrders : 0;

        // Calculate average order value
        double avgOrderValue = totalOrders > 0 ? totalSpent / totalOrders : 0;

        // Calculate eco efficiency score (lower is better)
        double ecoEfficiency = totalSpent > 0 ? totalCarbon / totalSpent : 0;

        // Enhanced eco score calculation
        int ecoScore = calculateAdvancedEcoScore(
                carbonSaved,
                greenPurchases,
                premiumEcoPurchases,
                totalOrders,
                repeatedCategories,
                ecoEfficiency
        );

        // Determine badge and rank
        String badge = determineBadge(ecoScore);

        // Calculate percentage of green purchases
        double greenPurchasePercentage = totalItems > 0 ? (greenPurchases * 100.0 / totalItems) : 0;

        // Set all stats
        stats.setTotalOrders(totalOrders);
        stats.setTotalCarbonFootprint(Math.round(totalCarbon * 100.0) / 100.0);
        stats.setTotalSpent(Math.round(totalSpent * 100.0) / 100.0);
        stats.setTotalItemsPurchased(totalItems);
        stats.setGreenPurchases(greenPurchases);
        stats.setCarbonSaved(Math.round(carbonSaved * 100.0) / 100.0);
        stats.setEcoScore(ecoScore);
        stats.setBadge(badge);

        return stats;
    }

    private int calculateAdvancedEcoScore(double carbonSaved, int greenPurchases,
                                          int premiumEcoPurchases, int totalOrders,
                                          int repeatedCategories, double ecoEfficiency) {
        // Multi-factor eco score calculation
        int score = 0;

        // Carbon savings (up to 500 points)
        score += Math.min(500, (int)(carbonSaved * 10));

        // Green purchases (10 points each)
        score += greenPurchases * 10;

        // Premium eco purchases (20 points each)
        score += premiumEcoPurchases * 20;

        // Loyalty bonus for repeat category purchases (5 points each)
        score += repeatedCategories * 5;

        // Order frequency bonus (5 points per order)
        score += totalOrders * 5;

        // Efficiency bonus (inverse of eco efficiency, up to 200 points)
        if (ecoEfficiency > 0 && ecoEfficiency < 1) {
            score += Math.min(200, (int)(100 / ecoEfficiency));
        }

        return Math.max(0, score);
    }

    private String determineBadge(int ecoScore) {
        if (ecoScore >= 2000) return "🏆 Eco Legend";
        else if (ecoScore >= 1500) return "🌟 Planet Hero";
        else if (ecoScore >= 1000) return "⭐ Sustainability Master";
        else if (ecoScore >= 750) return "🦸 Eco Warrior";
        else if (ecoScore >= 500) return "💚 Low Carbon Leader";
        else if (ecoScore >= 250) return "🌱 Eco Enthusiast";
        else if (ecoScore >= 100) return "🌿 Eco Starter";
        else return "🔰 Green Beginner";
    }

    private List<RecentOrderDTO> getRecentOrders(List<Order> orders, int limit) {
        return orders.stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .limit(limit)
                .map(order -> new RecentOrderDTO(
                        order.getId(),
                        order.getCreatedAt(),
                        order.getItems().size(),
                        order.getTotalAmount(),
                        order.getTotalCarbonFootprint(),
                        order.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    private List<AchievementDTO> calculateEnhancedAchievements(UserStatsDTO stats, List<Order> orders) {
        List<AchievementDTO> achievements = new ArrayList<>();

        // First order milestone
        achievements.add(new AchievementDTO(
                "first_order",
                "🎯 First Step",
                "Place your first eco-friendly order",
                "🎯",
                stats.getTotalOrders() >= 1,
                1,
                stats.getTotalOrders()
        ));

        // Green shopping milestones
        achievements.add(new AchievementDTO(
                "green_shopper_5",
                "🛍️ Green Shopper",
                "Make 5 green purchases (products with low carbon footprint)",
                "🛍️",
                stats.getGreenPurchases() >= 5,
                5,
                stats.getGreenPurchases()
        ));

        achievements.add(new AchievementDTO(
                "green_shopper_25",
                "🌟 Green Shopping Pro",
                "Make 25 green purchases",
                "🌟",
                stats.getGreenPurchases() >= 25,
                25,
                stats.getGreenPurchases()
        ));

        achievements.add(new AchievementDTO(
                "green_shopper_50",
                "💎 Green Shopping Master",
                "Make 50 green purchases",
                "💎",
                stats.getGreenPurchases() >= 50,
                50,
                stats.getGreenPurchases()
        ));

        // Carbon savings milestones
        achievements.add(new AchievementDTO(
                "carbon_saver_10",
                "♻️ Carbon Saver",
                "Save 10kg of CO2 emissions",
                "♻️",
                stats.getCarbonSaved() >= 10,
                10,
                stats.getCarbonSaved().intValue()
        ));

        achievements.add(new AchievementDTO(
                "carbon_saver_50",
                "🌍 Planet Protector",
                "Save 50kg of CO2 emissions",
                "🌍",
                stats.getCarbonSaved() >= 50,
                50,
                stats.getCarbonSaved().intValue()
        ));

        achievements.add(new AchievementDTO(
                "carbon_saver_100",
                "🌎 Climate Champion",
                "Save 100kg of CO2 emissions",
                "🌎",
                stats.getCarbonSaved() >= 100,
                100,
                stats.getCarbonSaved().intValue()
        ));

        achievements.add(new AchievementDTO(
                "carbon_saver_250",
                "🌏 Carbon Hero",
                "Save 250kg of CO2 emissions",
                "🌏",
                stats.getCarbonSaved() >= 250,
                250,
                stats.getCarbonSaved().intValue()
        ));

        // Eco score milestones
        achievements.add(new AchievementDTO(
                "eco_starter",
                "🌿 Eco Starter",
                "Reach 100 eco points",
                "🌿",
                stats.getEcoScore() >= 100,
                100,
                stats.getEcoScore()
        ));

        achievements.add(new AchievementDTO(
                "eco_enthusiast",
                "💚 Eco Enthusiast",
                "Reach 500 eco points",
                "💚",
                stats.getEcoScore() >= 500,
                500,
                stats.getEcoScore()
        ));

        achievements.add(new AchievementDTO(
                "eco_warrior",
                "🦸 Eco Warrior",
                "Reach 1000 eco points",
                "🦸",
                stats.getEcoScore() >= 1000,
                1000,
                stats.getEcoScore()
        ));

        achievements.add(new AchievementDTO(
                "eco_legend",
                "🏆 Eco Legend",
                "Reach 2000 eco points",
                "🏆",
                stats.getEcoScore() >= 2000,
                2000,
                stats.getEcoScore()
        ));

        // Order milestones
        achievements.add(new AchievementDTO(
                "regular_customer",
                "📦 Regular Customer",
                "Place 10 orders",
                "📦",
                stats.getTotalOrders() >= 10,
                10,
                stats.getTotalOrders()
        ));

        achievements.add(new AchievementDTO(
                "loyal_customer",
                "⭐ Loyal Customer",
                "Place 25 orders",
                "⭐",
                stats.getTotalOrders() >= 25,
                25,
                stats.getTotalOrders()
        ));

        achievements.add(new AchievementDTO(
                "vip_customer",
                "👑 VIP Customer",
                "Place 50 orders",
                "👑",
                stats.getTotalOrders() >= 50,
                50,
                stats.getTotalOrders()
        ));

        // Spending milestones
        achievements.add(new AchievementDTO(
                "spender_100",
                "💰 Eco Investor",
                "Spend $100 on eco products",
                "💰",
                stats.getTotalSpent() >= 100,
                100,
                stats.getTotalSpent().intValue()
        ));

        achievements.add(new AchievementDTO(
                "spender_500",
                "💵 Eco Patron",
                "Spend $500 on eco products",
                "💵",
                stats.getTotalSpent() >= 500,
                500,
                stats.getTotalSpent().intValue()
        ));

        achievements.add(new AchievementDTO(
                "spender_1000",
                "💎 Eco Advocate",
                "Spend $1000 on eco products",
                "💎",
                stats.getTotalSpent() >= 1000,
                1000,
                stats.getTotalSpent().intValue()
        ));

        // Sort: unlocked first, then by progress
        achievements.sort((a, b) -> {
            if (a.isUnlocked() != b.isUnlocked()) {
                return a.isUnlocked() ? -1 : 1;
            }
            double progressA = a.getRequiredValue() > 0 ? (double) a.getCurrentValue() / a.getRequiredValue() : 0;
            double progressB = b.getRequiredValue() > 0 ? (double) b.getCurrentValue() / b.getRequiredValue() : 0;
            return Double.compare(progressB, progressA);
        });

        return achievements;
    }

    private List<CarbonTipDTO> generateEnhancedPersonalizedTips(List<Order> orders, UserStatsDTO stats, List<Wishlist> wishlistItems) {
        List<CarbonTipDTO> tips = new ArrayList<>();

        // Analyze shopping patterns
        Map<String, Integer> categoryFrequency = new HashMap<>();
        Map<String, Double> categoryCarbonAvg = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                String category = item.getProduct().getCategory();
                double carbon = item.getCarbonSnapshot();

                categoryFrequency.merge(category, 1, Integer::sum);
                categoryCarbonAvg.merge(category, carbon, Double::sum);
                categoryCount.merge(category, 1, Integer::sum);
            }
        }

        // Calculate average carbon per category
        for (String category : categoryCarbonAvg.keySet()) {
            double total = categoryCarbonAvg.get(category);
            int count = categoryCount.get(category);
            categoryCarbonAvg.put(category, total / count);
        }

        // Beginner tips
        if (stats.getTotalOrders() < 3) {
            tips.add(new CarbonTipDTO(
                    "🌟",
                    "Welcome to eco-friendly shopping! Start by looking for A+ rated products to maximize your impact.",
                    "GETTING_STARTED",
                    "HIGH"
            ));
        }

        // Green purchase encouragement
        if (stats.getTotalItemsPurchased() > 0) {
            double greenPercentage = (stats.getGreenPurchases() * 100.0) / stats.getTotalItemsPurchased();
            if (greenPercentage < 30) {
                tips.add(new CarbonTipDTO(
                        "🌿",
                        String.format("Only %.0f%% of your purchases are 'green'. Try choosing products with carbon footprint under 2kg!", greenPercentage),
                        "SHOPPING",
                        "HIGH"
                ));
            } else if (greenPercentage < 60) {
                tips.add(new CarbonTipDTO(
                        "💚",
                        String.format("Great job! %.0f%% of your purchases are green. Keep it up!", greenPercentage),
                        "ENCOURAGEMENT",
                        "MEDIUM"
                ));
            } else {
                tips.add(new CarbonTipDTO(
                        "⭐",
                        String.format("Excellent! %.0f%% of your purchases are eco-friendly. You're a sustainability champion!", greenPercentage),
                        "CELEBRATION",
                        "LOW"
                ));
            }
        }

        // Category-specific tips
        String highestCarbonCategory = null;
        double highestAvgCarbon = 0;

        for (Map.Entry<String, Double> entry : categoryCarbonAvg.entrySet()) {
            if (entry.getValue() > highestAvgCarbon) {
                highestAvgCarbon = entry.getValue();
                highestCarbonCategory = entry.getKey();
            }
        }

        if (highestCarbonCategory != null && highestAvgCarbon > 5) {
            tips.add(new CarbonTipDTO(
                    "📊",
                    String.format("Your %s purchases have high carbon footprint (%.1fkg avg). Consider eco-alternatives in this category!",
                            highestCarbonCategory, highestAvgCarbon),
                    "OPTIMIZATION",
                    "HIGH"
            ));
        }

        // Wishlist insights
        if (!wishlistItems.isEmpty()) {
            double wishlistTotalCarbon = wishlistItems.stream()
                    .mapToDouble(w -> w.getProduct().getTotalCarbonFootprint())
                    .sum();

            long lowCarbonInWishlist = wishlistItems.stream()
                    .filter(w -> w.getProduct().getTotalCarbonFootprint() < 2.0)
                    .count();

            if (lowCarbonInWishlist < wishlistItems.size() / 2) {
                tips.add(new CarbonTipDTO(
                        "❤️",
                        String.format("Your wishlist contains %.1fkg CO2e. Consider prioritizing the %d low-carbon items!",
                                wishlistTotalCarbon, lowCarbonInWishlist),
                        "WISHLIST",
                        "MEDIUM"
                ));
            }
        }

        // Order frequency tip
        if (orders.size() >= 2) {
            LocalDateTime firstOrder = orders.stream()
                    .map(Order::getCreatedAt)
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());

            LocalDateTime lastOrder = orders.stream()
                    .map(Order::getCreatedAt)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());

            long daysBetween = ChronoUnit.DAYS.between(firstOrder, lastOrder);
            if (daysBetween > 0) {
                double ordersPerMonth = (orders.size() * 30.0) / daysBetween;
                if (ordersPerMonth > 4) {
                    tips.add(new CarbonTipDTO(
                            "📦",
                            "Consider consolidating your orders to reduce packaging waste and transportation emissions!",
                            "OPTIMIZATION",
                            "MEDIUM"
                    ));
                }
            }
        }

        // Carbon savings milestone tip
        int nextMilestone = 0;
        if (stats.getCarbonSaved() < 10) nextMilestone = 10;
        else if (stats.getCarbonSaved() < 50) nextMilestone = 50;
        else if (stats.getCarbonSaved() < 100) nextMilestone = 100;
        else if (stats.getCarbonSaved() < 250) nextMilestone = 250;

        if (nextMilestone > 0) {
            double remaining = nextMilestone - stats.getCarbonSaved();
            tips.add(new CarbonTipDTO(
                    "🎯",
                    String.format("You're %.1fkg away from saving %dkg CO2e! Keep making eco-friendly choices!",
                            remaining, nextMilestone),
                    "MILESTONE",
                    "MEDIUM"
            ));
        }

        // Achievement progress tip
        int unlockedAchievements = (int) calculateEnhancedAchievements(stats, orders).stream()
                .filter(AchievementDTO::isUnlocked)
                .count();
        int totalAchievements = calculateEnhancedAchievements(stats, orders).size();

        if (unlockedAchievements < totalAchievements) {
            tips.add(new CarbonTipDTO(
                    "🏆",
                    String.format("You've unlocked %d/%d achievements! Check your dashboard to see what's next!",
                            unlockedAchievements, totalAchievements),
                    "GAMIFICATION",
                    "LOW"
            ));
        }

        // Motivational tips
        tips.add(new CarbonTipDTO(
                "💚",
                "Every eco-friendly purchase helps reduce global carbon emissions. Thank you for making a difference!",
                "MOTIVATION",
                "LOW"
        ));

        // Sort by priority
        Map<String, Integer> priorityMap = Map.of("HIGH", 3, "MEDIUM", 2, "LOW", 1);
        tips.sort((a, b) -> priorityMap.getOrDefault(b.getPriority(), 0) - priorityMap.getOrDefault(a.getPriority(), 0));

        return tips.stream().limit(8).collect(Collectors.toList());
    }

    private CarbonTrendDTO calculateEnhancedCarbonTrend(List<Order> orders) {
        Map<String, Double> monthlyCarbon = new TreeMap<>();
        Map<String, Integer> monthlyCounts = new TreeMap<>();
        Map<String, Double> monthlySpending = new TreeMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // Get data for last 12 months
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twelveMonthsAgo = now.minusMonths(12);

        for (Order order : orders) {
            if (order.getCreatedAt().isAfter(twelveMonthsAgo)) {
                String monthKey = order.getCreatedAt().format(formatter);
                monthlyCarbon.merge(monthKey, order.getTotalCarbonFootprint(), Double::sum);
                monthlyCounts.merge(monthKey, 1, Integer::sum);
                monthlySpending.merge(monthKey, order.getTotalAmount(), Double::sum);
            }
        }

        // Fill in missing months with zeros
        for (int i = 11; i >= 0; i--) {
            String monthKey = now.minusMonths(i).format(formatter);
            monthlyCarbon.putIfAbsent(monthKey, 0.0);
            monthlyCounts.putIfAbsent(monthKey, 0);
            monthlySpending.putIfAbsent(monthKey, 0.0);
        }

        CarbonTrendDTO trend = new CarbonTrendDTO();

        // Format labels as "MMM YY"
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM yy");
        List<String> labels = new ArrayList<>();
        for (String key : monthlyCarbon.keySet()) {
            LocalDateTime date = LocalDateTime.parse(key + "-01T00:00:00");
            labels.add(date.format(displayFormatter));
        }

        trend.setLabels(labels);
        trend.setCarbonData(new ArrayList<>(monthlyCarbon.values()));
        trend.setOrderCounts(new ArrayList<>(monthlyCounts.values()));

        return trend;
    }

    private Map<String, Double> calculateDetailedCategoryBreakdown(List<Order> orders) {
        Map<String, Double> categoryCarbon = new HashMap<>();
        Map<String, Double> categorySpending = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                String category = item.getProduct().getCategory();
                double carbon = item.getCarbonSnapshot() * item.getQuantity();
                double spending = item.getPriceSnapshot() * item.getQuantity();

                categoryCarbon.merge(category, carbon, Double::sum);
                categorySpending.merge(category, spending, Double::sum);
                categoryCount.merge(category, 1, Integer::sum);
            }
        }

        // Round values
        for (String key : categoryCarbon.keySet()) {
            categoryCarbon.put(key, Math.round(categoryCarbon.get(key) * 100.0) / 100.0);
        }

        return categoryCarbon;
    }

    private Map<String, Integer> calculateDetailedEcoRatingDistribution(List<Order> orders) {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("A+", 0);
        distribution.put("A", 0);
        distribution.put("B", 0);
        distribution.put("C", 0);
        distribution.put("D", 0);

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                double carbon = item.getCarbonSnapshot();
                String rating;

                if (carbon < 1.0) rating = "A+";
                else if (carbon < 2.0) rating = "A";
                else if (carbon < 5.0) rating = "B";
                else if (carbon < 10.0) rating = "C";
                else rating = "D";

                distribution.merge(rating, item.getQuantity(), Integer::sum);
            }
        }

        return distribution;
    }

    // ============= SELLER DASHBOARD (UNCHANGED) =============
    public SellerDashboardDTO getSellerDashboard(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        SellerDashboardDTO dashboard = new SellerDashboardDTO();

        List<Product> products = productRepository.findBySellerId(sellerId);
        List<Order> allOrders = orderRepository.findAll();

        List<OrderItem> sellerOrderItems = new ArrayList<>();
        for (Order order : allOrders) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct().getSeller().getId().equals(sellerId)) {
                    sellerOrderItems.add(item);
                }
            }
        }

        dashboard.setStats(calculateSellerStats(products, sellerOrderItems));
        dashboard.setTopProducts(getTopProducts(sellerOrderItems, 5));
        dashboard.setRecentOrders(getSellerRecentOrders(sellerOrderItems, 5));
        dashboard.setSalesByCategory(calculateSalesByCategory(sellerOrderItems));
        dashboard.setRevenueBreakdown(calculateRevenueBreakdown(sellerOrderItems));

        return dashboard;
    }

    private SellerStatsDTO calculateSellerStats(List<Product> products, List<OrderItem> orderItems) {
        SellerStatsDTO stats = new SellerStatsDTO();

        stats.setTotalProducts(products.size());
        stats.setActiveProducts((int) products.stream().filter(Product::isActive).count());
        stats.setPendingProducts((int) products.stream().filter(p -> !p.isVerified()).count());
        stats.setVerifiedProducts((int) products.stream().filter(Product::isVerified).count());

        stats.setTotalSales(orderItems.size());

        double totalRevenue = orderItems.stream()
                .mapToDouble(item -> item.getPriceSnapshot() * item.getQuantity())
                .sum();
        stats.setTotalRevenue(totalRevenue);

        double avgCarbon = products.stream()
                .mapToDouble(Product::getTotalCarbonFootprint)
                .average()
                .orElse(0.0);
        stats.setAverageCarbonPerProduct(avgCarbon);

        return stats;
    }

    private List<ProductPerformanceDTO> getTopProducts(List<OrderItem> orderItems, int limit) {
        Map<Long, ProductPerformanceDTO> productMap = new HashMap<>();

        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            ProductPerformanceDTO perf = productMap.getOrDefault(product.getId(),
                    new ProductPerformanceDTO(product.getId(), product.getName(), 0, 0.0,
                            product.getTotalCarbonFootprint(), product.getEcoRating()));

            perf.setUnitsSold(perf.getUnitsSold() + item.getQuantity());
            perf.setRevenue(perf.getRevenue() + (item.getPriceSnapshot() * item.getQuantity()));

            productMap.put(product.getId(), perf);
        }

        return productMap.values().stream()
                .sorted((p1, p2) -> Double.compare(p2.getRevenue(), p1.getRevenue()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<RecentOrderDTO> getSellerRecentOrders(List<OrderItem> orderItems, int limit) {
        return orderItems.stream()
                .map(OrderItem::getOrder)
                .distinct()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .limit(limit)
                .map(order -> new RecentOrderDTO(
                        order.getId(),
                        order.getCreatedAt(),
                        order.getItems().size(),
                        order.getTotalAmount(),
                        order.getTotalCarbonFootprint(),
                        order.getStatus().toString()
                ))
                .collect(Collectors.toList());
    }

    private Map<String, Double> calculateSalesByCategory(List<OrderItem> orderItems) {
        Map<String, Double> salesByCategory = new HashMap<>();

        for (OrderItem item : orderItems) {
            String category = item.getProduct().getCategory();
            salesByCategory.merge(category,
                    item.getPriceSnapshot() * item.getQuantity(),
                    Double::sum);
        }

        return salesByCategory;
    }

    private RevenueBreakdownDTO calculateRevenueBreakdown(List<OrderItem> orderItems) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusWeeks(1);
        LocalDateTime monthStart = now.minusMonths(1);

        double todayRevenue = 0, weekRevenue = 0, monthRevenue = 0, totalRevenue = 0;

        for (OrderItem item : orderItems) {
            double revenue = item.getPriceSnapshot() * item.getQuantity();
            LocalDateTime orderDate = item.getOrder().getCreatedAt();

            totalRevenue += revenue;

            if (orderDate.isAfter(todayStart)) todayRevenue += revenue;
            if (orderDate.isAfter(weekStart)) weekRevenue += revenue;
            if (orderDate.isAfter(monthStart)) monthRevenue += revenue;
        }

        return new RevenueBreakdownDTO(todayRevenue, weekRevenue, monthRevenue, totalRevenue);
    }

    // ============= ADMIN DASHBOARD (UNCHANGED) =============
    public AdminDashboardDTO getAdminDashboard() {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();

        List<User> allUsers = userRepository.findAll();
        List<Product> allProducts = productRepository.findAll();
        List<Order> allOrders = orderRepository.findAll();
        List<AuditLog> recentAudits = auditLogRepository.findAll();

        dashboard.setPlatformStats(calculatePlatformStats(allUsers, allProducts, allOrders));
        dashboard.setPendingVerifications(getPendingVerifications(allProducts));
        dashboard.setTopSellers(calculateTopSellers(allOrders));
        dashboard.setRecentActivities(getRecentActivities(recentAudits, 10));
        dashboard.setCarbonImpact(calculatePlatformCarbonImpact(allOrders));
        dashboard.setUserRoleDistribution(calculateUserRoleDistribution(allUsers));

        return dashboard;
    }

    private PlatformStatsDTO calculatePlatformStats(List<User> users, List<Product> products, List<Order> orders) {
        PlatformStatsDTO stats = new PlatformStatsDTO();

        stats.setTotalUsers(users.size());
        stats.setTotalSellers((int) users.stream().filter(u -> u.getRole() == Role.SELLER).count());
        stats.setTotalProducts(products.size());
        stats.setTotalOrders(orders.size());
        stats.setActiveUsers((int) users.stream().filter(User::isActive).count());

        double totalRevenue = orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
        stats.setTotalRevenue(totalRevenue);

        double totalCarbon = orders.stream()
                .mapToDouble(Order::getTotalCarbonFootprint)
                .sum();
        stats.setPlatformCarbonFootprint(totalCarbon);

        double baselineCarbon = orders.size() * 10.0;
        stats.setPlatformCarbonSaved(Math.max(0, baselineCarbon - totalCarbon));

        return stats;
    }

    private List<PendingVerificationDTO> getPendingVerifications(List<Product> products) {
        return products.stream()
                .filter(p -> !p.isVerified())
                .map(p -> new PendingVerificationDTO(
                        p.getId(),
                        p.getName(),
                        p.getSeller().getId(),
                        p.getSeller().getFullName(),
                        p.getTotalCarbonFootprint(),
                        p.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    private List<TopSellerDTO> calculateTopSellers(List<Order> orders) {
        Map<Long, TopSellerDTO> sellerMap = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                User seller = item.getProduct().getSeller();
                TopSellerDTO sellerDTO = sellerMap.getOrDefault(seller.getId(),
                        new TopSellerDTO(seller.getId(), seller.getFullName(), 0, 0.0, 0.0));

                sellerDTO.setProductsSold(sellerDTO.getProductsSold() + item.getQuantity());
                sellerDTO.setRevenue(sellerDTO.getRevenue() + (item.getPriceSnapshot() * item.getQuantity()));

                sellerMap.put(seller.getId(), sellerDTO);
            }
        }

        for (TopSellerDTO seller : sellerMap.values()) {
            List<Product> sellerProducts = productRepository.findBySellerId(seller.getSellerId());
            double avgCarbon = sellerProducts.stream()
                    .mapToDouble(Product::getTotalCarbonFootprint)
                    .average()
                    .orElse(0.0);
            seller.setAverageCarbonImpact(avgCarbon);
        }

        return sellerMap.values().stream()
                .sorted((s1, s2) -> Double.compare(s2.getRevenue(), s1.getRevenue()))
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<RecentActivityDTO> getRecentActivities(List<AuditLog> logs, int limit) {
        return logs.stream()
                .sorted((l1, l2) -> l2.getTimestamp().compareTo(l1.getTimestamp()))
                .limit(limit)
                .map(log -> new RecentActivityDTO(
                        log.getActor().getFullName(),
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getTimestamp(),
                        log.getMetadata()
                ))
                .collect(Collectors.toList());
    }

    private CarbonImpactSummaryDTO calculatePlatformCarbonImpact(List<Order> orders) {
        CarbonImpactSummaryDTO impact = new CarbonImpactSummaryDTO();

        double totalCarbon = orders.stream()
                .mapToDouble(Order::getTotalCarbonFootprint)
                .sum();
        impact.setTotalCarbonFootprint(totalCarbon);

        double baselineCarbon = orders.size() * 10.0;
        impact.setTotalCarbonSaved(Math.max(0, baselineCarbon - totalCarbon));

        double avgCarbon = orders.isEmpty() ? 0 : totalCarbon / orders.size();
        impact.setAverageCarbonPerOrder(avgCarbon);

        int highImpact = 0, lowImpact = 0;
        Map<String, Double> carbonByCategory = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                if (item.getCarbonSnapshot() > 5.0) highImpact++;
                if (item.getCarbonSnapshot() < 2.0) lowImpact++;

                String category = item.getProduct().getCategory();
                carbonByCategory.merge(category,
                        item.getCarbonSnapshot() * item.getQuantity(),
                        Double::sum);
            }
        }

        impact.setHighImpactProducts(highImpact);
        impact.setLowImpactProducts(lowImpact);
        impact.setCarbonByCategory(carbonByCategory);

        return impact;
    }

    private Map<String, Integer> calculateUserRoleDistribution(List<User> users) {
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("USER", 0);
        distribution.put("SELLER", 0);
        distribution.put("ADMIN", 0);

        for (User user : users) {
            distribution.merge(user.getRole().toString(), 1, Integer::sum);
        }

        return distribution;
    }
}