package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.*;
import com.example.EcoBazaar_module2.repository.OrderItemRepository;
import com.example.EcoBazaar_module2.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService; // Assuming you have this to fetch user

    @Autowired
    private AuditService auditService;

    @Transactional
    public Order createOrderFromCart(Long userId, String address, String phone, String paymentMethod) {
        // 1. Get Cart
        Cart cart = cartService.getUserCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 2. Setup Order
        User user = userService.getUserById(userId); // Ensure this method exists in UserService

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING); // Initial status

        // Set the new simple fields
        order.setShippingAddress(address);
        order.setPhoneNumber(phone);
        order.setPaymentMethod(paymentMethod);

        // Simulate immediate payment for simplicity
        if ("COD".equals(paymentMethod)) {
            order.setPaymentStatus("PENDING");
        } else {
            order.setPaymentStatus("PAID");
        }

        double totalAmount = 0.0;
        double totalCarbon = 0.0;

        // 3. Move items from Cart to Order
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceSnapshot(product.getPrice());
            orderItem.setCarbonSnapshot(product.getTotalCarbonFootprint());
            orderItem.setProductNameSnapshot(product.getName());

            order.getItems().add(orderItem);

            totalAmount += product.getPrice() * cartItem.getQuantity();
            totalCarbon += product.getTotalCarbonFootprint() * cartItem.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        order.setTotalCarbonFootprint(totalCarbon);

        // 4. Save Order
        Order savedOrder = orderRepository.save(order);

        // 5. Clear Cart
        cartService.clearCart(userId);

        // 6. Log
        auditService.log(userId, "ORDER_CREATED", "ORDER", savedOrder.getId(),
                "Order placed via " + paymentMethod);

        return savedOrder;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}