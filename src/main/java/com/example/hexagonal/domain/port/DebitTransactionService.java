package com.example.hexagonal.domain.port;

import com.example.hexagonal.domain.model.DebitTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DebitTransactionService {
    DebitTransaction createDebitTransaction(String companyId, String companyDocument, String companyName, 
                                          String bankAccountId, BigDecimal amount, String description, 
                                          LocalDateTime scheduledDate, String correlationId);
    
    DebitTransaction processDebitTransaction(UUID transactionId);
    
    DebitTransaction retryFailedTransaction(UUID transactionId);
    
    DebitTransaction cancelTransaction(UUID transactionId, String reason);
    
    DebitTransaction getTransactionById(UUID transactionId);
    
    List<DebitTransaction> getTransactionsByCompanyId(String companyId);
    
    List<DebitTransaction> getTransactionsByStatus(TransactionStatus status);
    
    List<DebitTransaction> getScheduledTransactions(LocalDateTime startDate, LocalDateTime endDate);
    
    List<DebitTransaction> getFailedTransactionsForRetry();
    
    long getTransactionCountByCompanyAndStatus(String companyId, TransactionStatus status);
}
