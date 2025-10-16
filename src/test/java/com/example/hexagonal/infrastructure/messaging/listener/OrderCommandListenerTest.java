package com.example.hexagonal.infrastructure.messaging.listener;

import com.example.hexagonal.domain.port.OrderService;
import com.example.hexagonal.infrastructure.messaging.dto.CancelOrderCommand;
import com.example.hexagonal.infrastructure.messaging.dto.CreateOrderCommand;
import com.example.hexagonal.infrastructure.messaging.dto.UpdateOrderStatusCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCommandListenerTest {

    @Mock
    private OrderService orderService;

    private OrderCommandListener listener;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        listener = new OrderCommandListener(orderService, objectMapper);
    }

    @Test
    void handleOrderCommand_WithCreateOrder_ShouldProcessSuccessfully() throws Exception {
        CreateOrderCommand command = CreateOrderCommand.builder()
                .commandId(UUID.randomUUID())
                .correlationId(UUID.randomUUID().toString())
                .customerId("customer1")
                .productId("product1")
                .quantity(5)
                .build();

        String message = objectMapper.writeValueAsString(command);

        listener.handleOrderCommand(message);

        verify(orderService).createOrder("customer1", "product1", 5);
    }

    @Test
    void handleOrderCommand_WithUpdateOrderStatus_ShouldProcessSuccessfully() throws Exception {
        UpdateOrderStatusCommand command = UpdateOrderStatusCommand.builder()
                .commandId(UUID.randomUUID())
                .correlationId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID())
                .status(OrderStatus.PROCESSING)
                .build();

        String message = objectMapper.writeValueAsString(command);

        listener.handleOrderCommand(message);

        verify(orderService).updateOrderStatus(command.getOrderId(), OrderStatus.PROCESSING);
    }

    @Test
    void handleOrderCommand_WithCancelOrder_ShouldProcessSuccessfully() throws Exception {
        CancelOrderCommand command = CancelOrderCommand.builder()
                .commandId(UUID.randomUUID())
                .correlationId(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID())
                .build();

        String message = objectMapper.writeValueAsString(command);

        listener.handleOrderCommand(message);

        verify(orderService).cancelOrder(command.getOrderId());
    }

    @Test
    void handleOrderCommand_WithUnknownCommand_ShouldLogWarning() {
        String unknownMessage = "{\"type\":\"UNKNOWN_COMMAND\"}";

        listener.handleOrderCommand(unknownMessage);

        verify(orderService, never()).createOrder(any(), any(), any());
        verify(orderService, never()).updateOrderStatus(any(), any());
        verify(orderService, never()).cancelOrder(any());
    }

    @Test
    void handleOrderCommand_WithInvalidJson_ShouldThrowException() {
        String invalidJson = "invalid json";

        try {
            listener.handleOrderCommand(invalidJson);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Failed to process order command");
        }

        verify(orderService, never()).createOrder(any(), any(), any());
    }
}
