package com.example.hexagonal.infrastructure.api.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class UpdateOrderStatusRequest {
    @NotNull(message = "Order status is required")
    private OrderStatus status;
}
