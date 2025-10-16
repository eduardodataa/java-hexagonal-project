package com.example.hexagonal.infrastructure.messaging.adapter;

import com.example.hexagonal.domain.model.OrderEvent;
import com.example.hexagonal.domain.port.EventPublisher;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsEventPublisherAdapter implements EventPublisher {
    
    private final SqsTemplate sqsTemplate;
    
    @Value("${aws.sqs.queue-name:order-events}")
    private String queueName;
    
    @Override
    public void publish(OrderEvent event) {
        try {
            sqsTemplate.send(queueName, event);
            log.info("Event published successfully: {} for order: {}", event.getEventType(), event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish event: {} for order: {}", event.getEventType(), event.getOrderId(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
