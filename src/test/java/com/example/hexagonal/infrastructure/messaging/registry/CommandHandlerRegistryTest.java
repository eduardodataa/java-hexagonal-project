package com.example.hexagonal.infrastructure.messaging.registry;

import com.example.hexagonal.infrastructure.messaging.handler.CommandHandler;
import com.example.hexagonal.infrastructure.messaging.handler.CreateDebitTransactionHandler;
import com.example.hexagonal.infrastructure.messaging.handler.ProcessDebitTransactionHandler;
import com.example.hexagonal.infrastructure.messaging.handler.RetryDebitTransactionHandler;
import com.example.hexagonal.infrastructure.messaging.handler.CancelDebitTransactionHandler;
import com.example.hexagonal.infrastructure.messaging.handler.ValidateDebitTransactionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandHandlerRegistryTest {

    @Mock
    private CreateDebitTransactionHandler createHandler;

    @Mock
    private ProcessDebitTransactionHandler processHandler;

    @Mock
    private RetryDebitTransactionHandler retryHandler;

    @Mock
    private CancelDebitTransactionHandler cancelHandler;

    @Mock
    private ValidateDebitTransactionHandler validateHandler;

    private CommandHandlerRegistry registry;

    @BeforeEach
    void setUp() {
        when(createHandler.getCommandType()).thenReturn("CREATE_DEBIT_TRANSACTION");
        when(processHandler.getCommandType()).thenReturn("PROCESS_DEBIT_TRANSACTION");
        when(retryHandler.getCommandType()).thenReturn("RETRY_DEBIT_TRANSACTION");
        when(cancelHandler.getCommandType()).thenReturn("CANCEL_DEBIT_TRANSACTION");
        when(validateHandler.getCommandType()).thenReturn("VALIDATE_DEBIT_TRANSACTION");

        List<CommandHandler<?>> handlers = List.of(
                createHandler, processHandler, retryHandler, cancelHandler, validateHandler
        );

        registry = new CommandHandlerRegistry(handlers);
        registry.initializeHandlers();
    }

    @Test
    void shouldRegisterAllHandlers() {
        assertThat(registry.getSupportedCommandTypes()).hasSize(5);
        assertThat(registry.getSupportedCommandTypes()).contains(
                "CREATE_DEBIT_TRANSACTION",
                "PROCESS_DEBIT_TRANSACTION",
                "RETRY_DEBIT_TRANSACTION",
                "CANCEL_DEBIT_TRANSACTION",
                "VALIDATE_DEBIT_TRANSACTION"
        );
    }

    @Test
    void shouldReturnCorrectHandler() {
        CommandHandler<?> handler = registry.getHandler("CREATE_DEBIT_TRANSACTION");
        assertThat(handler).isEqualTo(createHandler);
    }

    @Test
    void shouldReturnNullForUnknownCommand() {
        CommandHandler<?> handler = registry.getHandler("UNKNOWN_COMMAND");
        assertThat(handler).isNull();
    }

    @Test
    void shouldCheckHandlerExistence() {
        assertThat(registry.hasHandler("CREATE_DEBIT_TRANSACTION")).isTrue();
        assertThat(registry.hasHandler("UNKNOWN_COMMAND")).isFalse();
    }

    @Test
    void shouldDemonstrateOpenClosedPrinciple() {
        assertThat(registry.getSupportedCommandTypes()).contains("VALIDATE_DEBIT_TRANSACTION");
        
        CommandHandler<?> validateHandler = registry.getHandler("VALIDATE_DEBIT_TRANSACTION");
        assertThat(validateHandler).isNotNull();
        assertThat(validateHandler.getCommandType()).isEqualTo("VALIDATE_DEBIT_TRANSACTION");
    }
}
