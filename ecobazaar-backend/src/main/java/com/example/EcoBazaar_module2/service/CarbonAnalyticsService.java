package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.Order;
import com.example.EcoBazaar_module2.model.OrderItem;
import com.example.EcoBazaar_module2.repository.OrderRepository;
import com.example.EcoBazaar_module2.repository.ProductRepository; // <--- NEW IMPORT
import com.example.EcoBazaar_module2.repository.UserRepository;    // <--- NEW IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarbonAnalyticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;       // <--- INJECTED

    @Autowired
    private ProductRepository productRepository; // <--- INJECTED

    public Map<String, Object> getUserCarbonReport(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        double totalCarbonFootprint = orders.stream()
                .mapToDouble(Order::getTotalCarbonFootprint)
                .sum();

        int greenPurchases = 0;
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                if (item.getCarbonSnapshot() < 2.0) {
                    greenPurchases++;
                }
            }
        }

        double baselineCarbon = orders.size() * 10.0;
        double carbonSaved = Math.max(0, baselineCarbon - totalCarbonFootprint);

        int ecoScore = (int) (carbonSaved * 10);
        String badge = getBadge(ecoScore);

        Map<String, Object> report = new HashMap<>();
        report.put("totalCarbonFootprint", totalCarbonFootprint);
        report.put("carbonSaved", carbonSaved);
        report.put("greenPurchases", greenPurchases);
        report.put("ecoScore", ecoScore);
        report.put("badge", badge);
        report.put("totalOrders", orders.size());

        return report;
    }

    public Map<String, Object> getPlatformCarbonSummary() {
        List<Order> allOrders = orderRepository.findAll();

        double totalCarbon = allOrders.stream()
                .mapToDouble(Order::getTotalCarbonFootprint)
                .sum();

        double totalRevenue = allOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        Map<String, Object> summary = new HashMap<>();

        // --- NEW LOGIC START ---
        long totalUsers = userRepository.count();       // Count all users in DB
        long totalProducts = productRepository.count(); // Count all products in DB

        summary.put("totalUsers", totalUsers);
        summary.put("totalProducts", totalProducts);
        // --- NEW LOGIC END ---

        summary.put("totalOrders", allOrders.size());
        summary.put("totalCarbonFootprint", totalCarbon);
        summary.put("totalRevenue", totalRevenue);
        summary.put("averageCarbonPerOrder", allOrders.isEmpty() ? 0 : totalCarbon / allOrders.size());

        return summary;
    }

    private String getBadge(int ecoScore) {
        if (ecoScore > 1000) return "Planet Protector";
        else if (ecoScore > 500) return "Low Carbon Leader";
        else if (ecoScore > 100) return "Eco Enthusiast";
        else return "Eco Starter";
    }
}