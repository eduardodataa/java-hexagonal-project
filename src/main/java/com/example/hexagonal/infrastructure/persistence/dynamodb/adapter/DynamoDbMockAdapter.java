package com.example.hexagonal.infrastructure.persistence.dynamodb.adapter;

import com.example.hexagonal.domain.model.DebitTransaction;
import com.example.hexagonal.domain.model.TransactionStatus;
import com.example.hexagonal.domain.port.DynamoDbRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DynamoDbMockAdapter implements DynamoDbRepository {
    
    private final Map<UUID, DebitTransaction> mockDatabase = new ConcurrentHashMap<>();
    
    @Override
    public DebitTransaction save(DebitTransaction transaction) {
        log.info("DynamoDB Mock: Saving transaction {} for company {}", 
                transaction.getTransactionId(), transaction.getCompanyId());
        
        mockDatabase.put(transaction.getTransactionId(), transaction);
        
        log.info("DynamoDB Mock: Transaction {} saved successfully", transaction.getTransactionId());
        return transaction;
    }
    
    @Override
    public Optional<DebitTransaction> findById(UUID transactionId) {
        log.info("DynamoDB Mock: Finding transaction by ID: {}", transactionId);
        
        DebitTransaction transaction = mockDatabase.get(transactionId);
        
        if (transaction != null) {
            log.info("DynamoDB Mock: Transaction {} found", transactionId);
        } else {
            log.info("DynamoDB Mock: Transaction {} not found", transactionId);
        }
        
        return Optional.ofNullable(transaction);
    }
    
    @Override
    public List<DebitTransaction> findByCompanyId(String companyId) {
        log.info("DynamoDB Mock: Finding transactions by company ID: {}", companyId);
        
        List<DebitTransaction> transactions = mockDatabase.values().stream()
                .filter(t -> companyId.equals(t.getCompanyId()))
                .collect(Collectors.toList());
        
        log.info("DynamoDB Mock: Found {} transactions for company {}", transactions.size(), companyId);
        return transactions;
    }
    
    @Override
    public List<DebitTransaction> findByStatus(TransactionStatus status) {
        log.info("DynamoDB Mock: Finding transactions by status: {}", status);
        
        List<DebitTransaction> transactions = mockDatabase.values().stream()
                .filter(t -> status.equals(t.getStatus()))
                .collect(Collectors.toList());
        
        log.info("DynamoDB Mock: Found {} transactions with status {}", transactions.size(), status);
        return transactions;
    }
    
    @Override
    public void deleteById(UUID transactionId) {
        log.info("DynamoDB Mock: Deleting transaction: {}", transactionId);
        
        DebitTransaction removed = mockDatabase.remove(transactionId);
        
        if (removed != null) {
            log.info("DynamoDB Mock: Transaction {} deleted successfully", transactionId);
        } else {
            log.info("DynamoDB Mock: Transaction {} not found for deletion", transactionId);
        }
    }
    
    @Override
    public boolean existsById(UUID transactionId) {
        log.info("DynamoDB Mock: Checking if transaction exists: {}", transactionId);
        
        boolean exists = mockDatabase.containsKey(transactionId);
        log.info("DynamoDB Mock: Transaction {} exists: {}", transactionId, exists);
        
        return exists;
    }
    
    @Override
    public long countByCompanyId(String companyId) {
        log.info("DynamoDB Mock: Counting transactions for company: {}", companyId);
        
        long count = mockDatabase.values().stream()
                .filter(t -> companyId.equals(t.getCompanyId()))
                .count();
        
        log.info("DynamoDB Mock: Company {} has {} transactions", companyId, count);
        return count;
    }
    
    public void clearMockData() {
        log.info("DynamoDB Mock: Clearing all mock data");
        mockDatabase.clear();
    }
    
    public int getMockDataSize() {
        return mockDatabase.size();
    }
}
