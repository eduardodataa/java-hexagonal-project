package com.example.hexagonal.infrastructure.messaging.listener;

import com.example.hexagonal.domain.model.OrderEvent;
import com.example.hexagonal.infrastructure.messaging.dto.CancelOrderCommand;
import com.example.hexagonal.infrastructure.messaging.dto.CreateOrderCommand;
import com.example.hexagonal.infrastructure.messaging.dto.UpdateOrderStatusCommand;
import com.example.hexagonal.domain.port.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCommandListener {
    
    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    
    @SqsListener("order-commands")
    public void handleOrderCommand(String message) {
        try {
            log.info("Received order command: {}", message);
            
            if (message.contains("CREATE_ORDER")) {
                CreateOrderCommand command = objectMapper.readValue(message, CreateOrderCommand.class);
                handleCreateOrderCommand(command);
            } else if (message.contains("UPDATE_ORDER_STATUS")) {
                UpdateOrderStatusCommand command = objectMapper.readValue(message, UpdateOrderStatusCommand.class);
                handleUpdateOrderStatusCommand(command);
            } else if (message.contains("CANCEL_ORDER")) {
                CancelOrderCommand command = objectMapper.readValue(message, CancelOrderCommand.class);
                handleCancelOrderCommand(command);
            } else {
                log.warn("Unknown command type in message: {}", message);
            }
            
        } catch (Exception e) {
            log.error("Error processing order command: {}", message, e);
            throw new RuntimeException("Failed to process order command", e);
        }
    }
    
    private void handleCreateOrderCommand(CreateOrderCommand command) {
        log.info("Processing create order command for customer: {}", command.getCustomerId());
        
        try {
            orderService.createOrder(
                command.getCustomerId(),
                command.getProductId(),
                command.getQuantity()
            );
            log.info("Order created successfully for customer: {}", command.getCustomerId());
        } catch (Exception e) {
            log.error("Failed to create order for customer: {}", command.getCustomerId(), e);
            throw e;
        }
    }
    
    private void handleUpdateOrderStatusCommand(UpdateOrderStatusCommand command) {
        log.info("Processing update order status command for order: {}", command.getOrderId());
        
        try {
            orderService.updateOrderStatus(command.getOrderId(), command.getStatus());
            log.info("Order status updated successfully for order: {}", command.getOrderId());
        } catch (Exception e) {
            log.error("Failed to update order status for order: {}", command.getOrderId(), e);
            throw e;
        }
    }
    
    private void handleCancelOrderCommand(CancelOrderCommand command) {
        log.info("Processing cancel order command for order: {}", command.getOrderId());
        
        try {
            orderService.cancelOrder(command.getOrderId());
            log.info("Order cancelled successfully for order: {}", command.getOrderId());
        } catch (Exception e) {
            log.error("Failed to cancel order: {}", command.getOrderId(), e);
            throw e;
        }
    }
}
