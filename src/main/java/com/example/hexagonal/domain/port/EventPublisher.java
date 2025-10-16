package com.example.hexagonal.domain.port;

import com.example.hexagonal.domain.model.OrderEvent;

public interface EventPublisher {
    void publish(OrderEvent event);
}
