package com.example.hexagonal.infrastructure.api.controller;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderStatus;
import com.example.hexagonal.domain.port.OrderService;
import com.example.hexagonal.infrastructure.api.dto.CreateOrderRequest;
import com.example.hexagonal.infrastructure.api.dto.OrderDto;
import com.example.hexagonal.infrastructure.api.dto.UpdateOrderStatusRequest;
import com.example.hexagonal.infrastructure.api.mapper.OrderApiMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final OrderApiMapper orderApiMapper;
    
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(
                request.getCustomerId(),
                request.getProductId(),
                request.getQuantity()
        );
        OrderDto orderDto = orderApiMapper.toDto(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable UUID id) {
        Order order = orderService.getOrderById(id);
        OrderDto orderDto = orderApiMapper.toDto(order);
        return ResponseEntity.ok(orderDto);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDto>> getOrdersByCustomerId(@PathVariable String customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        List<OrderDto> orderDtos = orderApiMapper.toDtoList(orders);
        return ResponseEntity.ok(orderDtos);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        List<OrderDto> orderDtos = orderApiMapper.toDtoList(orders);
        return ResponseEntity.ok(orderDtos);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        Order order = orderService.updateOrderStatus(id, request.getStatus());
        OrderDto orderDto = orderApiMapper.toDto(order);
        return ResponseEntity.ok(orderDto);
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable UUID id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
