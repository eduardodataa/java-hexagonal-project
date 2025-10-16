package com.example.hexagonal.infrastructure.persistence;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderStatus;
import com.example.hexagonal.infrastructure.persistence.adapter.OrderRepositoryAdapter;
import com.example.hexagonal.infrastructure.persistence.entity.OrderEntity;
import com.example.hexagonal.infrastructure.persistence.mapper.OrderMapper;
import com.example.hexagonal.infrastructure.persistence.repository.OrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryAdapterIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderJpaRepository jpaRepository;

    private OrderRepositoryAdapter adapter;
    private OrderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderMapper();
        adapter = new OrderRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    void save_ShouldPersistOrderInDatabase() {
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .customerId("customer1")
                .productId("product1")
                .quantity(5)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = adapter.save(order);

        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isEqualTo(order.getId());
        assertThat(savedOrder.getCustomerId()).isEqualTo("customer1");

        OrderEntity persistedEntity = entityManager.find(OrderEntity.class, order.getId());
        assertThat(persistedEntity).isNotNull();
        assertThat(persistedEntity.getCustomerId()).isEqualTo("customer1");
    }

    @Test
    void findById_ShouldReturnPersistedOrder() {
        OrderEntity entity = new OrderEntity();
        entity.setId(UUID.randomUUID());
        entity.setCustomerId("customer1");
        entity.setProductId("product1");
        entity.setQuantity(5);
        entity.setStatus(com.example.hexagonal.infrastructure.persistence.entity.OrderStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        entityManager.persistAndFlush(entity);

        Optional<Order> result = adapter.findById(entity.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCustomerId()).isEqualTo("customer1");
        assertThat(result.get().getProductId()).isEqualTo("product1");
        assertThat(result.get().getQuantity()).isEqualTo(5);
    }

    @Test
    void findByCustomerId_ShouldReturnOrdersForCustomer() {
        OrderEntity entity1 = createOrderEntity("customer1", "product1");
        OrderEntity entity2 = createOrderEntity("customer1", "product2");
        OrderEntity entity3 = createOrderEntity("customer2", "product1");

        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        entityManager.persistAndFlush(entity3);

        List<Order> result = adapter.findByCustomerId("customer1");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Order::getCustomerId).containsOnly("customer1");
    }

    @Test
    void findByStatus_ShouldReturnOrdersWithStatus() {
        OrderEntity entity1 = createOrderEntity("customer1", "product1");
        entity1.setStatus(com.example.hexagonal.infrastructure.persistence.entity.OrderStatus.PENDING);
        
        OrderEntity entity2 = createOrderEntity("customer2", "product2");
        entity2.setStatus(com.example.hexagonal.infrastructure.persistence.entity.OrderStatus.PROCESSING);

        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);

        List<Order> result = adapter.findByStatus(OrderStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void deleteById_ShouldRemoveOrderFromDatabase() {
        OrderEntity entity = createOrderEntity("customer1", "product1");
        entityManager.persistAndFlush(entity);

        adapter.deleteById(entity.getId());

        OrderEntity deletedEntity = entityManager.find(OrderEntity.class, entity.getId());
        assertThat(deletedEntity).isNull();
    }

    private OrderEntity createOrderEntity(String customerId, String productId) {
        OrderEntity entity = new OrderEntity();
        entity.setId(UUID.randomUUID());
        entity.setCustomerId(customerId);
        entity.setProductId(productId);
        entity.setQuantity(5);
        entity.setStatus(com.example.hexagonal.infrastructure.persistence.entity.OrderStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}
