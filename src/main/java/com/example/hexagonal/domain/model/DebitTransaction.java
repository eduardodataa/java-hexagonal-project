package com.example.hexagonal.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DebitTransaction {
    private UUID transactionId;
    private String companyId;
    private String companyDocument;
    private String companyName;
    private String bankAccountId;
    private BigDecimal amount;
    private String description;
    private TransactionStatus status;
    private String failureReason;
    private LocalDateTime scheduledDate;
    private LocalDateTime processedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String correlationId;
    private Integer retryCount;
}
