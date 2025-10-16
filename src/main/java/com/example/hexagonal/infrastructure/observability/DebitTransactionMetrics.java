package com.example.hexagonal.infrastructure.observability;

import datadog.trace.api.Trace;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DebitTransactionMetrics {
    
    private final Counter debitTransactionCreatedCounter;
    private final Counter debitTransactionProcessedCounter;
    private final Counter debitTransactionFailedCounter;
    private final Counter debitTransactionRetryCounter;
    private final Timer debitTransactionProcessingTimer;
    private final Timer debitTransactionCreationTimer;
    
    @Trace("debit.transaction.created")
    public void recordDebitTransactionCreated() {
        debitTransactionCreatedCounter.increment();
    }
    
    @Trace("debit.transaction.processed")
    public void recordDebitTransactionProcessed() {
        debitTransactionProcessedCounter.increment();
    }
    
    @Trace("debit.transaction.failed")
    public void recordDebitTransactionFailed() {
        debitTransactionFailedCounter.increment();
    }
    
    @Trace("debit.transaction.retry")
    public void recordDebitTransactionRetry() {
        debitTransactionRetryCounter.increment();
    }
    
    @Trace("debit.transaction.processing.time")
    public Timer.Sample startDebitTransactionProcessingTimer() {
        return Timer.start(debitTransactionProcessingTimer);
    }
    
    @Trace("debit.transaction.creation.time")
    public Timer.Sample startDebitTransactionCreationTimer() {
        return Timer.start(debitTransactionCreationTimer);
    }
    
    public void recordDebitTransactionProcessingTime(Timer.Sample sample) {
        sample.stop();
    }
    
    public void recordDebitTransactionCreationTime(Timer.Sample sample) {
        sample.stop();
    }
}
