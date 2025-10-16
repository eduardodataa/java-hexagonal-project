package com.example.hexagonal.domain.port;

import com.example.hexagonal.domain.model.DebitEvent;

public interface EventPublisher {
    void publish(DebitEvent event);
}
