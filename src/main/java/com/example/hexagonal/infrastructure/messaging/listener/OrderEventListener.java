package com.example.hexagonal.infrastructure.messaging.listener;

import com.example.hexagonal.domain.model.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {
    
    @SqsListener("order-events")
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received order event: {} for order: {}", event.getEventType(), event.getOrderId());
        
        switch (event.getEventType()) {
            case "ORDER_CREATED" -> handleOrderCreated(event);
            case "ORDER_STATUS_UPDATED" -> handleOrderStatusUpdated(event);
            case "ORDER_CANCELLED" -> handleOrderCancelled(event);
            default -> log.warn("Unknown event type: {}", event.getEventType());
        }
    }
    
    private void handleOrderCreated(OrderEvent event) {
        log.info("Processing order created event for order: {}", event.getOrderId());
    }
    
    private void handleOrderStatusUpdated(OrderEvent event) {
        log.info("Processing order status updated event for order: {}", event.getOrderId());
    }
    
    private void handleOrderCancelled(OrderEvent event) {
        log.info("Processing order cancelled event for order: {}", event.getOrderId());
    }
}
