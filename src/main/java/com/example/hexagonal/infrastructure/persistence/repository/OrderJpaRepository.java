package com.example.hexagonal.infrastructure.persistence.repository;

import com.example.hexagonal.infrastructure.persistence.entity.OrderEntity;
import com.example.hexagonal.infrastructure.persistence.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByCustomerId(String customerId);
    List<OrderEntity> findByStatus(OrderStatus status);
}
