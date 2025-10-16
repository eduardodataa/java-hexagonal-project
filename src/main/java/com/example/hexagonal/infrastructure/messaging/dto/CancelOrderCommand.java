package com.example.hexagonal.infrastructure.messaging.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class CancelDebitTransactionCommand {
    @NotNull(message = "Transaction ID is required")
    private UUID transactionId;
    
    @NotBlank(message = "Cancellation reason is required")
    private String reason;
    
    private UUID commandId;
    private String correlationId;
}
