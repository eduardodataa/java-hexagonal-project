package com.example.hexagonal.domain.port;

import com.example.hexagonal.domain.model.DebitTransaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebitTransactionRepository {
    DebitTransaction save(DebitTransaction transaction);
    Optional<DebitTransaction> findById(UUID transactionId);
    List<DebitTransaction> findByCompanyId(String companyId);
    List<DebitTransaction> findByStatus(TransactionStatus status);
    List<DebitTransaction> findByScheduledDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<DebitTransaction> findFailedTransactionsForRetry();
    void deleteById(UUID transactionId);
    long countByCompanyIdAndStatus(String companyId, TransactionStatus status);
}
