package com.example.hexagonal.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderEvent {
    private UUID eventId;
    private UUID orderId;
    private String eventType;
    private String payload;
    private LocalDateTime timestamp;
}
