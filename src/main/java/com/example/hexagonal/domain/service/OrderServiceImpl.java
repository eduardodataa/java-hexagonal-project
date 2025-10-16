package com.example.hexagonal.domain.service;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderEvent;
import com.example.hexagonal.domain.model.OrderStatus;
import com.example.hexagonal.domain.port.EventPublisher;
import com.example.hexagonal.domain.port.OrderRepository;
import com.example.hexagonal.domain.port.OrderService;
import com.example.hexagonal.infrastructure.observability.OrderMetrics;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    private final OrderMetrics orderMetrics;
    
    @Override
    public Order createOrder(String customerId, String productId, Integer quantity) {
        Timer.Sample sample = orderMetrics.startOrderProcessingTimer();
        
        try {
            Order order = Order.builder()
                    .id(UUID.randomUUID())
                    .customerId(customerId)
                    .productId(productId)
                    .quantity(quantity)
                    .status(OrderStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            Order savedOrder = orderRepository.save(order);
            
            OrderEvent event = OrderEvent.builder()
                    .eventId(UUID.randomUUID())
                    .orderId(savedOrder.getId())
                    .eventType("ORDER_CREATED")
                    .payload(String.format("Order %s created for customer %s", savedOrder.getId(), customerId))
                    .timestamp(LocalDateTime.now())
                    .build();
            
            eventPublisher.publish(event);
            orderMetrics.recordOrderCreated();
            
            return savedOrder;
        } finally {
            orderMetrics.recordOrderProcessingTime(sample);
        }
    }
    
    @Override
    public Order updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        Order updatedOrder = orderRepository.save(order);
        
        OrderEvent event = OrderEvent.builder()
                .eventId(UUID.randomUUID())
                .orderId(orderId)
                .eventType("ORDER_STATUS_UPDATED")
                .payload(String.format("Order %s status updated to %s", orderId, status))
                .timestamp(LocalDateTime.now())
                .build();
        
        eventPublisher.publish(event);
        orderMetrics.recordOrderStatusUpdated();
        
        return updatedOrder;
    }
    
    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    @Override
    public List<Order> getOrdersByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    @Override
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed order");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        
        orderRepository.save(order);
        
        OrderEvent event = OrderEvent.builder()
                .eventId(UUID.randomUUID())
                .orderId(orderId)
                .eventType("ORDER_CANCELLED")
                .payload(String.format("Order %s cancelled", orderId))
                .timestamp(LocalDateTime.now())
                .build();
        
        eventPublisher.publish(event);
    }
}
