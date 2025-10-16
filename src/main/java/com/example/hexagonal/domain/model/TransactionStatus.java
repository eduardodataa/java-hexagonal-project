package com.example.hexagonal.domain.model;

public enum TransactionStatus {
    PENDING,
    SCHEDULED,
    PROCESSING,
    PROCESSED,
    FAILED,
    CANCELLED,
    RETRYING
}
