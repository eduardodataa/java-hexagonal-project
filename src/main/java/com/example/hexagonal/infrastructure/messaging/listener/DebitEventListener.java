package com.example.hexagonal.infrastructure.messaging.listener;

import com.example.hexagonal.domain.model.DebitEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DebitEventListener {
    
    @SqsListener("debit-events")
    public void handleDebitEvent(DebitEvent event) {
        log.info("Received debit event: {} for transaction: {}", event.getEventType(), event.getTransactionId());
        
        switch (event.getEventType()) {
            case "DEBIT_TRANSACTION_CREATED" -> handleDebitTransactionCreated(event);
            case "DEBIT_TRANSACTION_PROCESSING" -> handleDebitTransactionProcessing(event);
            case "DEBIT_TRANSACTION_RETRYING" -> handleDebitTransactionRetrying(event);
            case "DEBIT_TRANSACTION_CANCELLED" -> handleDebitTransactionCancelled(event);
            default -> log.warn("Unknown event type: {}", event.getEventType());
        }
    }
    
    private void handleDebitTransactionCreated(DebitEvent event) {
        log.info("Processing debit transaction created event for transaction: {} company: {}", 
                event.getTransactionId(), event.getCompanyId());
    }
    
    private void handleDebitTransactionProcessing(DebitEvent event) {
        log.info("Processing debit transaction processing event for transaction: {} company: {}", 
                event.getTransactionId(), event.getCompanyId());
    }
    
    private void handleDebitTransactionRetrying(DebitEvent event) {
        log.info("Processing debit transaction retrying event for transaction: {} company: {}", 
                event.getTransactionId(), event.getCompanyId());
    }
    
    private void handleDebitTransactionCancelled(DebitEvent event) {
        log.info("Processing debit transaction cancelled event for transaction: {} company: {}", 
                event.getTransactionId(), event.getCompanyId());
    }
}