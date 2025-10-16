package com.example.hexagonal.config;

import datadog.trace.api.Trace;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityConfig {
    
    @Bean
    public Counter orderCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("orders.created")
                .description("Number of orders created")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter orderStatusUpdatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("orders.status.updated")
                .description("Number of order status updates")
                .register(meterRegistry);
    }
    
    @Bean
    public Timer orderProcessingTimer(MeterRegistry meterRegistry) {
        return Timer.builder("orders.processing.time")
                .description("Time taken to process orders")
                .register(meterRegistry);
    }
}
