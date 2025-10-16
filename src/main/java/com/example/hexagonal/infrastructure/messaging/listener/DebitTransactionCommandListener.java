package com.example.hexagonal.infrastructure.messaging.listener;

import com.example.hexagonal.infrastructure.messaging.handler.CommandHandler;
import com.example.hexagonal.infrastructure.messaging.registry.CommandHandlerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DebitTransactionCommandListener {
    
    private final CommandHandlerRegistry commandHandlerRegistry;
    
    @SqsListener("debit-commands")
    public void handleDebitTransactionCommand(String message) {
        try {
            log.info("Received debit transaction command: {}", message);
            
            String commandType = extractCommandType(message);
            
            CommandHandler<?> handler = commandHandlerRegistry.getHandler(commandType);
            if (handler != null) {
                handler.processCommand(message);
            } else {
                log.warn("Unknown command type: {}. Supported types: {}", 
                        commandType, 
                        commandHandlerRegistry.getSupportedCommandTypes());
            }
            
        } catch (Exception e) {
            log.error("Error processing debit transaction command: {}", message, e);
            throw new RuntimeException("Failed to process debit transaction command", e);
        }
    }
    
    private String extractCommandType(String message) {
        for (String commandType : commandHandlerRegistry.getSupportedCommandTypes()) {
            if (message.contains(commandType)) {
                return commandType;
            }
        }
        return "UNKNOWN";
    }
}
