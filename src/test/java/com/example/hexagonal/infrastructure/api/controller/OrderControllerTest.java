package com.example.hexagonal.infrastructure.api.controller;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderStatus;
import com.example.hexagonal.domain.port.OrderService;
import com.example.hexagonal.infrastructure.api.dto.CreateOrderRequest;
import com.example.hexagonal.infrastructure.api.dto.OrderDto;
import com.example.hexagonal.infrastructure.api.dto.UpdateOrderStatusRequest;
import com.example.hexagonal.infrastructure.api.mapper.OrderApiMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderApiMapper orderApiMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;
    private OrderDto testOrderDto;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .id(UUID.randomUUID())
                .customerId("customer1")
                .productId("product1")
                .quantity(5)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testOrderDto = OrderDto.builder()
                .id(testOrder.getId())
                .customerId(testOrder.getCustomerId())
                .productId(testOrder.getProductId())
                .quantity(testOrder.getQuantity())
                .status(testOrder.getStatus())
                .createdAt(testOrder.getCreatedAt())
                .updatedAt(testOrder.getUpdatedAt())
                .build();
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId("customer1")
                .productId("product1")
                .quantity(5)
                .build();

        when(orderService.createOrder(any(), any(), any())).thenReturn(testOrder);
        when(orderApiMapper.toDto(testOrder)).thenReturn(testOrderDto);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testOrder.getId().toString()))
                .andExpect(jsonPath("$.customerId").value("customer1"))
                .andExpect(jsonPath("$.productId").value("product1"))
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createOrder_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId("")
                .productId("product1")
                .quantity(-1)
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderById_ShouldReturnOrder() throws Exception {
        when(orderService.getOrderById(testOrder.getId())).thenReturn(testOrder);
        when(orderApiMapper.toDto(testOrder)).thenReturn(testOrderDto);

        mockMvc.perform(get("/api/orders/{id}", testOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrder.getId().toString()))
                .andExpect(jsonPath("$.customerId").value("customer1"));
    }

    @Test
    void getOrdersByCustomerId_ShouldReturnOrders() throws Exception {
        List<Order> orders = List.of(testOrder);
        List<OrderDto> orderDtos = List.of(testOrderDto);

        when(orderService.getOrdersByCustomerId("customer1")).thenReturn(orders);
        when(orderApiMapper.toDtoList(orders)).thenReturn(orderDtos);

        mockMvc.perform(get("/api/orders/customer/{customerId}", "customer1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerId").value("customer1"));
    }

    @Test
    void getOrdersByStatus_ShouldReturnOrders() throws Exception {
        List<Order> orders = List.of(testOrder);
        List<OrderDto> orderDtos = List.of(testOrderDto);

        when(orderService.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(orders);
        when(orderApiMapper.toDtoList(orders)).thenReturn(orderDtos);

        mockMvc.perform(get("/api/orders/status/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrder() throws Exception {
        UpdateOrderStatusRequest request = UpdateOrderRequest.builder()
                .status(OrderStatus.PROCESSING)
                .build();

        Order updatedOrder = Order.builder()
                .id(testOrder.getId())
                .customerId(testOrder.getCustomerId())
                .productId(testOrder.getProductId())
                .quantity(testOrder.getQuantity())
                .status(OrderStatus.PROCESSING)
                .createdAt(testOrder.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        OrderDto updatedOrderDto = OrderDto.builder()
                .id(updatedOrder.getId())
                .customerId(updatedOrder.getCustomerId())
                .productId(updatedOrder.getProductId())
                .quantity(updatedOrder.getQuantity())
                .status(updatedOrder.getStatus())
                .createdAt(updatedOrder.getCreatedAt())
                .updatedAt(updatedOrder.getUpdatedAt())
                .build();

        when(orderService.updateOrderStatus(eq(testOrder.getId()), any())).thenReturn(updatedOrder);
        when(orderApiMapper.toDto(updatedOrder)).thenReturn(updatedOrderDto);

        mockMvc.perform(put("/api/orders/{id}/status", testOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void cancelOrder_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/api/orders/{id}/cancel", testOrder.getId()))
                .andExpect(status().isNoContent());
    }
}
