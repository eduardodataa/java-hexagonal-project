package com.example.hexagonal.domain.port;

import com.example.hexagonal.domain.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createOrder(String customerId, String productId, Integer quantity);
    Order updateOrderStatus(UUID orderId, OrderStatus status);
    Order getOrderById(UUID orderId);
    List<Order> getOrdersByCustomerId(String customerId);
    List<Order> getOrdersByStatus(OrderStatus status);
    void cancelOrder(UUID orderId);
}
