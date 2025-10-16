package com.example.hexagonal.infrastructure.observability;

import datadog.trace.api.Trace;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMetrics {
    
    private final Counter orderCreatedCounter;
    private final Counter orderStatusUpdatedCounter;
    private final Timer orderProcessingTimer;
    
    @Trace("order.created")
    public void recordOrderCreated() {
        orderCreatedCounter.increment();
    }
    
    @Trace("order.status.updated")
    public void recordOrderStatusUpdated() {
        orderStatusUpdatedCounter.increment();
    }
    
    @Trace("order.processing.time")
    public Timer.Sample startOrderProcessingTimer() {
        return Timer.start(orderProcessingTimer);
    }
    
    public void recordOrderProcessingTime(Timer.Sample sample) {
        sample.stop();
    }
}
