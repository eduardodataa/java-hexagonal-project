package com.example.hexagonal.domain.service;

import com.example.hexagonal.domain.model.DebitTransaction;
import com.example.hexagonal.domain.model.DebitEvent;
import com.example.hexagonal.domain.model.TransactionStatus;
import com.example.hexagonal.domain.port.EventPublisher;
import com.example.hexagonal.domain.port.DebitTransactionRepository;
import com.example.hexagonal.domain.port.DebitTransactionService;
import com.example.hexagonal.infrastructure.observability.DebitTransactionMetrics;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DebitTransactionServiceImpl implements DebitTransactionService {
    
    private final DebitTransactionRepository transactionRepository;
    private final EventPublisher eventPublisher;
    private final DebitTransactionMetrics debitTransactionMetrics;
    
    @Override
    public DebitTransaction createDebitTransaction(String companyId, String companyDocument, String companyName, 
                                                  String bankAccountId, BigDecimal amount, String description, 
                                                  LocalDateTime scheduledDate, String correlationId) {
        Timer.Sample sample = debitTransactionMetrics.startDebitTransactionCreationTimer();
        
        try {
            DebitTransaction transaction = DebitTransaction.builder()
                    .transactionId(UUID.randomUUID())
                    .companyId(companyId)
                    .companyDocument(companyDocument)
                    .companyName(companyName)
                    .bankAccountId(bankAccountId)
                    .amount(amount)
                    .description(description)
                    .status(TransactionStatus.PENDING)
                    .scheduledDate(scheduledDate)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .correlationId(correlationId)
                    .retryCount(0)
                    .build();
            
            DebitTransaction savedTransaction = transactionRepository.save(transaction);
            
            DebitEvent event = DebitEvent.builder()
                    .eventId(UUID.randomUUID())
                    .transactionId(savedTransaction.getTransactionId())
                    .eventType("DEBIT_TRANSACTION_CREATED")
                    .payload(String.format("Debit transaction %s created for company %s", 
                            savedTransaction.getTransactionId(), companyId))
                    .timestamp(LocalDateTime.now())
                    .correlationId(correlationId)
                    .companyId(companyId)
                    .build();
            
            eventPublisher.publish(event);
            debitTransactionMetrics.recordDebitTransactionCreated();
            
            return savedTransaction;
        } finally {
            debitTransactionMetrics.recordDebitTransactionCreationTime(sample);
        }
    }
    
    @Override
    public DebitTransaction processDebitTransaction(UUID transactionId) {
        DebitTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (transaction.getStatus() != TransactionStatus.PENDING && 
            transaction.getStatus() != TransactionStatus.SCHEDULED) {
            throw new RuntimeException("Transaction cannot be processed in current status: " + transaction.getStatus());
        }
        
        transaction.setStatus(TransactionStatus.PROCESSING);
        transaction.setUpdatedAt(LocalDateTime.now());
        
        DebitTransaction updatedTransaction = transactionRepository.save(transaction);
        
        DebitEvent event = DebitEvent.builder()
                .eventId(UUID.randomUUID())
                .transactionId(transactionId)
                .eventType("DEBIT_TRANSACTION_PROCESSING")
                .payload(String.format("Debit transaction %s processing started", transactionId))
                .timestamp(LocalDateTime.now())
                .correlationId(transaction.getCorrelationId())
                .companyId(transaction.getCompanyId())
                .build();
        
        eventPublisher.publish(event);
        debitTransactionMetrics.recordDebitTransactionProcessed();
        
        return updatedTransaction;
    }
    
    @Override
    public DebitTransaction retryFailedTransaction(UUID transactionId) {
        DebitTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (transaction.getStatus() != TransactionStatus.FAILED) {
            throw new RuntimeException("Only failed transactions can be retried");
        }
        
        if (transaction.getRetryCount() >= 3) {
            throw new RuntimeException("Maximum retry attempts exceeded");
        }
        
        transaction.setStatus(TransactionStatus.RETRYING);
        transaction.setRetryCount(transaction.getRetryCount() + 1);
        transaction.setUpdatedAt(LocalDateTime.now());
        
        DebitTransaction updatedTransaction = transactionRepository.save(transaction);
        
        DebitEvent event = DebitEvent.builder()
                .eventId(UUID.randomUUID())
                .transactionId(transactionId)
                .eventType("DEBIT_TRANSACTION_RETRYING")
                .payload(String.format("Debit transaction %s retry attempt %d", 
                        transactionId, transaction.getRetryCount()))
                .timestamp(LocalDateTime.now())
                .correlationId(transaction.getCorrelationId())
                .companyId(transaction.getCompanyId())
                .build();
        
        eventPublisher.publish(event);
        
        return updatedTransaction;
    }
    
    @Override
    public DebitTransaction cancelTransaction(UUID transactionId, String reason) {
        DebitTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (transaction.getStatus() == TransactionStatus.PROCESSED) {
            throw new RuntimeException("Cannot cancel processed transaction");
        }
        
        transaction.setStatus(TransactionStatus.CANCELLED);
        transaction.setFailureReason(reason);
        transaction.setUpdatedAt(LocalDateTime.now());
        
        DebitTransaction updatedTransaction = transactionRepository.save(transaction);
        
        DebitEvent event = DebitEvent.builder()
                .eventId(UUID.randomUUID())
                .transactionId(transactionId)
                .eventType("DEBIT_TRANSACTION_CANCELLED")
                .payload(String.format("Debit transaction %s cancelled: %s", transactionId, reason))
                .timestamp(LocalDateTime.now())
                .correlationId(transaction.getCorrelationId())
                .companyId(transaction.getCompanyId())
                .build();
        
        eventPublisher.publish(event);
        
        return updatedTransaction;
    }
    
    @Override
    public DebitTransaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
    
    @Override
    public List<DebitTransaction> getTransactionsByCompanyId(String companyId) {
        return transactionRepository.findByCompanyId(companyId);
    }
    
    @Override
    public List<DebitTransaction> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }
    
    @Override
    public List<DebitTransaction> getScheduledTransactions(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByScheduledDateBetween(startDate, endDate);
    }
    
    @Override
    public List<DebitTransaction> getFailedTransactionsForRetry() {
        return transactionRepository.findFailedTransactionsForRetry();
    }
    
    @Override
    public long getTransactionCountByCompanyAndStatus(String companyId, TransactionStatus status) {
        return transactionRepository.countByCompanyIdAndStatus(companyId, status);
    }
}
