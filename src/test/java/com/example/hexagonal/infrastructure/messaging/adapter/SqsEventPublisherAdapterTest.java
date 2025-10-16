package com.example.hexagonal.infrastructure.messaging.adapter;

import com.example.hexagonal.domain.model.OrderEvent;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqsEventPublisherAdapterTest {

    @Mock
    private SqsTemplate sqsTemplate;

    private SqsEventPublisherAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SqsEventPublisherAdapter(sqsTemplate);
        ReflectionTestUtils.setField(adapter, "queueName", "test-order-events");
    }

    @Test
    void publish_ShouldPublishEventSuccessfully() {
        OrderEvent event = OrderEvent.builder()
                .eventId(UUID.randomUUID())
                .orderId(UUID.randomUUID())
                .eventType("ORDER_CREATED")
                .payload("test payload")
                .timestamp(LocalDateTime.now())
                .build();

        adapter.publish(event);

        verify(sqsTemplate).send(eq("test-order-events"), eq(event));
    }

    @Test
    void publish_WhenExceptionOccurs_ShouldThrowRuntimeException() {
        OrderEvent event = OrderEvent.builder()
                .eventId(UUID.randomUUID())
                .orderId(UUID.randomUUID())
                .eventType("ORDER_CREATED")
                .payload("test payload")
                .timestamp(LocalDateTime.now())
                .build();

        doThrow(new RuntimeException("SQS error")).when(sqsTemplate).send(any(), any());

        try {
            adapter.publish(event);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Failed to publish event");
            assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
            assertThat(e.getCause().getMessage()).isEqualTo("SQS error");
        }

        verify(sqsTemplate).send(eq("test-order-events"), eq(event));
    }
}
