package com.example.hexagonal.infrastructure.persistence.adapter;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderStatus;
import com.example.hexagonal.infrastructure.persistence.entity.OrderEntity;
import com.example.hexagonal.infrastructure.persistence.mapper.OrderMapper;
import com.example.hexagonal.infrastructure.persistence.repository.OrderJpaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryAdapterTest {

    @Mock
    private OrderJpaRepository jpaRepository;

    @Mock
    private OrderMapper mapper;

    private OrderRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new OrderRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    void save_ShouldSaveOrderAndReturnMappedOrder() {
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .customerId("customer1")
                .productId("product1")
                .quantity(5)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        OrderEntity entity = new OrderEntity();
        OrderEntity savedEntity = new OrderEntity();
        Order mappedOrder = Order.builder().build();

        when(mapper.toEntity(order)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(mappedOrder);

        Order result = adapter.save(order);

        assertThat(result).isEqualTo(mappedOrder);
        verify(mapper).toEntity(order);
        verify(jpaRepository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    void findById_WhenOrderExists_ShouldReturnOrder() {
        UUID orderId = UUID.randomUUID();
        OrderEntity entity = new OrderEntity();
        Order order = Order.builder().build();

        when(jpaRepository.findById(orderId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(order);

        Optional<Order> result = adapter.findById(orderId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(order);
        verify(jpaRepository).findById(orderId);
        verify(mapper).toDomain(entity);
    }

    @Test
    void findById_WhenOrderDoesNotExist_ShouldReturnEmpty() {
        UUID orderId = UUID.randomUUID();
        when(jpaRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<Order> result = adapter.findById(orderId);

        assertThat(result).isEmpty();
        verify(jpaRepository).findById(orderId);
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void findByCustomerId_ShouldReturnOrders() {
        String customerId = "customer1";
        List<OrderEntity> entities = List.of(new OrderEntity(), new OrderEntity());
        List<Order> orders = List.of(Order.builder().build(), Order.builder().build());

        when(jpaRepository.findByCustomerId(customerId)).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(orders);

        List<Order> result = adapter.findByCustomerId(customerId);

        assertThat(result).isEqualTo(orders);
        verify(jpaRepository).findByCustomerId(customerId);
        verify(mapper).toDomainList(entities);
    }

    @Test
    void findByStatus_ShouldReturnOrders() {
        OrderStatus status = OrderStatus.PENDING;
        List<OrderEntity> entities = List.of(new OrderEntity(), new OrderEntity());
        List<Order> orders = List.of(Order.builder().build(), Order.builder().build());

        when(jpaRepository.findByStatus(any())).thenReturn(entities);
        when(mapper.toDomainList(entities)).thenReturn(orders);

        List<Order> result = adapter.findByStatus(status);

        assertThat(result).isEqualTo(orders);
        verify(jpaRepository).findByStatus(any());
        verify(mapper).toDomainList(entities);
    }

    @Test
    void deleteById_ShouldDeleteOrder() {
        UUID orderId = UUID.randomUUID();

        adapter.deleteById(orderId);

        verify(jpaRepository).deleteById(orderId);
    }
}
