package com.example.hexagonal.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DebitEvent {
    private UUID eventId;
    private UUID transactionId;
    private String eventType;
    private String payload;
    private LocalDateTime timestamp;
    private String correlationId;
    private String companyId;
}
