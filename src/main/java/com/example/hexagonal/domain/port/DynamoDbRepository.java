package com.example.hexagonal.domain.port;

import com.example.hexagonal.domain.model.DebitTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DynamoDbRepository {
    DebitTransaction save(DebitTransaction transaction);
    Optional<DebitTransaction> findById(UUID transactionId);
    List<DebitTransaction> findByCompanyId(String companyId);
    List<DebitTransaction> findByStatus(TransactionStatus status);
    void deleteById(UUID transactionId);
    boolean existsById(UUID transactionId);
    long countByCompanyId(String companyId);
}
