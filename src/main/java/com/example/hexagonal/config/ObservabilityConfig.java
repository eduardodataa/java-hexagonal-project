package com.example.hexagonal.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityConfig {
    
    @Bean
    public Counter debitTransactionCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("debit.transactions.created")
                .description("Number of debit transactions created")
                .tag("service", "hexagonal-debit-service")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter debitTransactionProcessedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("debit.transactions.processed")
                .description("Number of debit transactions processed")
                .tag("service", "hexagonal-debit-service")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter debitTransactionFailedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("debit.transactions.failed")
                .description("Number of debit transactions failed")
                .tag("service", "hexagonal-debit-service")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter debitTransactionRetryCounter(MeterRegistry meterRegistry) {
        return Counter.builder("debit.transactions.retry")
                .description("Number of debit transaction retries")
                .tag("service", "hexagonal-debit-service")
                .register(meterRegistry);
    }
    
    @Bean
    public Timer debitTransactionProcessingTimer(MeterRegistry meterRegistry) {
        return Timer.builder("debit.transactions.processing.time")
                .description("Time taken to process debit transactions")
                .tag("service", "hexagonal-debit-service")
                .register(meterRegistry);
    }
    
    @Bean
    public Timer debitTransactionCreationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("debit.transactions.creation.time")
                .description("Time taken to create debit transactions")
                .tag("service", "hexagonal-debit-service")
                .register(meterRegistry);
    }
}
