package com.example.hexagonal.infrastructure.messaging.dto;

import com.example.hexagonal.domain.model.OrderStatus;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    CANCELLED
}
