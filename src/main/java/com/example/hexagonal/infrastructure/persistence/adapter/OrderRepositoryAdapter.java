package com.example.hexagonal.infrastructure.persistence.adapter;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderStatus;
import com.example.hexagonal.domain.port.OrderRepository;
import com.example.hexagonal.infrastructure.persistence.entity.OrderEntity;
import com.example.hexagonal.infrastructure.persistence.mapper.OrderMapper;
import com.example.hexagonal.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {
    
    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;
    
    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Order> findByCustomerId(String customerId) {
        List<OrderEntity> entities = jpaRepository.findByCustomerId(customerId);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        com.example.hexagonal.infrastructure.persistence.entity.OrderStatus entityStatus = 
                mapStatusToEntity(status);
        List<OrderEntity> entities = jpaRepository.findByStatus(entityStatus);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    private com.example.hexagonal.infrastructure.persistence.entity.OrderStatus mapStatusToEntity(OrderStatus domainStatus) {
        return switch (domainStatus) {
            case PENDING -> com.example.hexagonal.infrastructure.persistence.entity.OrderStatus.PENDING;
            case PROCESSING -> com.example.hexagonal.infrastructure.persistence.entity.OrderStatus.PROCESSING;
            case COMPLETED -> com.example.hexagonal.infrastructure.persistence.entity.OrderStatus.COMPLETED;
            case CANCELLED -> com.example.hexagonal.infrastructure.persistence.entity.OrderStatus.CANCELLED;
        };
    }
}
