package com.example.EcoBazaar_module2.controller;

import com.example.EcoBazaar_module2.model.Order;
import com.example.EcoBazaar_module2.model.OrderStatus;
import com.example.EcoBazaar_module2.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> createOrder(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String address = request.get("address");
            String phone = request.get("phone");
            String paymentMethod = request.getOrDefault("paymentMethod", "Credit Card");

            if (address == null || address.isEmpty()) {
                return ResponseEntity.badRequest().body("Address is required");
            }

            Order order = orderService.createOrderFromCart(userId, address, phone, paymentMethod);

            // Return a simple success response instead of the entire order
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order created successfully");
            response.put("orderId", order.getId());
            response.put("orderDate", order.getCreatedAt());
            response.put("totalAmount", order.getTotalAmount());
            response.put("totalItems", order.getItems().size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the full error
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage(),
                            "timestamp", LocalDateTime.now().toString())
            );
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
}