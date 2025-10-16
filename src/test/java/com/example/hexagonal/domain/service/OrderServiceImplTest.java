package com.example.hexagonal.domain.service;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderEvent;
import com.example.hexagonal.domain.model.OrderStatus;
import com.example.hexagonal.domain.port.EventPublisher;
import com.example.hexagonal.domain.port.OrderRepository;
import com.example.hexagonal.infrastructure.observability.OrderMetrics;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private OrderMetrics orderMetrics;

    @Mock
    private Timer.Sample timerSample;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, eventPublisher, orderMetrics);
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        when(orderMetrics.startOrderProcessingTimer()).thenReturn(timerSample);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder("customer1", "product1", 5);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo("customer1");
        assertThat(result.getProductId()).isEqualTo("product1");
        assertThat(result.getQuantity()).isEqualTo(5);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();

        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publish(any(OrderEvent.class));
        verify(orderMetrics).recordOrderCreated();
        verify(orderMetrics).recordOrderProcessingTime(timerSample);
    }

    @Test
    void updateOrderStatus_ShouldUpdateOrderStatusSuccessfully() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = Order.builder()
                .id(orderId)
                .customerId("customer1")
                .productId("product1")
                .quantity(5)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.updateOrderStatus(orderId, OrderStatus.PROCESSING);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(result.getUpdatedAt()).isNotNull();

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publish(any(OrderEvent.class));
        verify(orderMetrics).recordOrderStatusUpdated();
    }

    @Test
    void updateOrderStatus_WhenOrderNotFound_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, OrderStatus.PROCESSING))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found");

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publish(any(OrderEvent.class));
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
                .id(orderId)
                .customerId("customer1")
                .productId("product1")
                .quantity(5)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(orderId);

        assertThat(result).isEqualTo(order);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderById_WhenOrderNotFound_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found");

        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrdersByCustomerId_ShouldReturnOrders() {
        String customerId = "customer1";
        List<Order> orders = List.of(
                Order.builder().id(UUID.randomUUID()).customerId(customerId).build(),
                Order.builder().id(UUID.randomUUID()).customerId(customerId).build()
        );

        when(orderRepository.findByCustomerId(customerId)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByCustomerId(customerId);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(orders);
        verify(orderRepository).findByCustomerId(customerId);
    }

    @Test
    void getOrdersByStatus_ShouldReturnOrders() {
        OrderStatus status = OrderStatus.PENDING;
        List<Order> orders = List.of(
                Order.builder().id(UUID.randomUUID()).status(status).build(),
                Order.builder().id(UUID.randomUUID()).status(status).build()
        );

        when(orderRepository.findByStatus(status)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByStatus(status);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(orders);
        verify(orderRepository).findByStatus(status);
    }

    @Test
    void cancelOrder_ShouldCancelOrderSuccessfully() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = Order.builder()
                .id(orderId)
                .customerId("customer1")
                .productId("product1")
                .quantity(5)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.cancelOrder(orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publish(any(OrderEvent.class));
    }

    @Test
    void cancelOrder_WhenOrderNotFound_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found");

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publish(any(OrderEvent.class));
    }

    @Test
    void cancelOrder_WhenOrderIsCompleted_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();
        Order completedOrder = Order.builder()
                .id(orderId)
                .customerId("customer1")
                .productId("product1")
                .quantity(5)
                .status(OrderStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(completedOrder));

        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot cancel completed order");

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publish(any(OrderEvent.class));
    }
}
