package com.example.hexagonal.infrastructure.persistence.mapper;

import com.example.hexagonal.domain.model.Order;
import com.example.hexagonal.domain.model.OrderStatus as DomainOrderStatus;
import com.example.hexagonal.infrastructure.persistence.entity.OrderEntity;
import com.example.hexagonal.infrastructure.persistence.entity.OrderStatus as EntityOrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    
    public Order toDomain(OrderEntity entity) {
        return Order.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .status(mapStatusToDomain(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    public OrderEntity toEntity(Order domain) {
        OrderEntity entity = new OrderEntity();
        entity.setId(domain.getId());
        entity.setCustomerId(domain.getCustomerId());
        entity.setProductId(domain.getProductId());
        entity.setQuantity(domain.getQuantity());
        entity.setStatus(mapStatusToEntity(domain.getStatus()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
    
    public List<Order> toDomainList(List<OrderEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    private DomainOrderStatus mapStatusToDomain(EntityOrderStatus entityStatus) {
        return switch (entityStatus) {
            case PENDING -> DomainOrderStatus.PENDING;
            case PROCESSING -> DomainOrderStatus.PROCESSING;
            case COMPLETED -> DomainOrderStatus.COMPLETED;
            case CANCELLED -> DomainOrderStatus.CANCELLED;
        };
    }
    
    private EntityOrderStatus mapStatusToEntity(DomainOrderStatus domainStatus) {
        return switch (domainStatus) {
            case PENDING -> EntityOrderStatus.PENDING;
            case PROCESSING -> EntityOrderStatus.PROCESSING;
            case COMPLETED -> EntityOrderStatus.COMPLETED;
            case CANCELLED -> EntityOrderStatus.CANCELLED;
        };
    }
}
