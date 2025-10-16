package com.example.hexagonal.infrastructure.messaging.registry;

import com.example.hexagonal.infrastructure.messaging.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandHandlerRegistry {
    
    private final List<CommandHandler<?>> commandHandlers;
    private Map<String, CommandHandler<?>> handlerMap;
    
    @PostConstruct
    public void initializeHandlers() {
        handlerMap = commandHandlers.stream()
                .collect(Collectors.toMap(
                    CommandHandler::getCommandType,
                    Function.identity()
                ));
        
        log.info("Initialized {} command handlers: {}", 
                handlerMap.size(), 
                handlerMap.keySet());
    }
    
    public CommandHandler<?> getHandler(String commandType) {
        CommandHandler<?> handler = handlerMap.get(commandType);
        if (handler == null) {
            log.warn("No handler found for command type: {}", commandType);
        }
        return handler;
    }
    
    public boolean hasHandler(String commandType) {
        return handlerMap.containsKey(commandType);
    }
    
    public List<String> getSupportedCommandTypes() {
        return List.copyOf(handlerMap.keySet());
    }
}
