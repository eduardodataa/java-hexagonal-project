package com.example.hexagonal.infrastructure.messaging.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreateDebitTransactionCommand {
    @NotBlank(message = "Company ID is required")
    private String companyId;
    
    @NotBlank(message = "Company document is required")
    private String companyDocument;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @NotBlank(message = "Bank account ID is required")
    private String bankAccountId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Scheduled date is required")
    private LocalDateTime scheduledDate;
    
    private UUID commandId;
    private String correlationId;
}
