package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.model.Order;
import com.example.EcoBazaar_module2.model.Product;
import com.example.EcoBazaar_module2.repository.OrderRepository;
import com.example.EcoBazaar_module2.repository.ProductRepository;
import com.example.EcoBazaar_module2.repository.UserRepository;
import com.example.EcoBazaar_module2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    /**
     * GET /api/admin/stats
     * Returns platform metrics: totalUsers, totalProducts, totalOrders, totalCarbonSaved, pendingApprovals
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            long totalUsers = userRepository.count();
            long totalProducts = productRepository.count();
            long totalOrders = orderRepository.count();

            List<Order> orders = orderRepository.findAll();
            double totalCarbonSaved = 0.0;
            if (orders != null) {
                for (Order order : orders) {
                    if (order != null && order.getTotalCarbonFootprint() != null) {
                        totalCarbonSaved += order.getTotalCarbonFootprint();
                    }
                }
            }

            List<Product> pendingProducts = productService.getPendingProducts();
            long pendingCount = pendingProducts != null ? pendingProducts.size() : 0;

            stats.put("totalUsers", totalUsers);
            stats.put("totalProducts", totalProducts);
            stats.put("totalOrders", totalOrders);
            stats.put("totalCarbonSaved", totalCarbonSaved);
            stats.put("pendingApprovals", pendingCount);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            stats.put("totalUsers", 0L);
            stats.put("totalProducts", 0L);
            stats.put("totalOrders", 0L);
            stats.put("totalCarbonSaved", 0.0);
            stats.put("pendingApprovals", 0L);
            return ResponseEntity.ok(stats);
        }
    }
}
