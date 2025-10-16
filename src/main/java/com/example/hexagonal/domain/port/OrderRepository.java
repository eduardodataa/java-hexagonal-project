package com.example.hexagonal.domain.port;

import com.example.hexagonal.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findByCustomerId(String customerId);
    List<Order> findByStatus(OrderStatus status);
    void deleteById(UUID id);
}
