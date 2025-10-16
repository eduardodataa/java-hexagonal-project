package com.example.hexagonal.infrastructure.messaging.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class RetryDebitTransactionCommand {
    @NotNull(message = "Transaction ID is required")
    private UUID transactionId;
    
    private UUID commandId;
    private String correlationId;
}
