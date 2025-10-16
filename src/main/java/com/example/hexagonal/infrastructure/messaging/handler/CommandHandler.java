package com.example.hexagonal.infrastructure.messaging.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class CommandHandler<T> {
    
    protected final ObjectMapper objectMapper;
    
    public abstract String getCommandType();
    
    public abstract Class<T> getCommandClass();
    
    public abstract void handle(T command);
    
    public void processCommand(String message) {
        try {
            T command = objectMapper.readValue(message, getCommandClass());
            handle(command);
        } catch (Exception e) {
            log.error("Error processing {} command: {}", getCommandType(), message, e);
            throw new RuntimeException("Failed to process " + getCommandType() + " command", e);
        }
    }
}
