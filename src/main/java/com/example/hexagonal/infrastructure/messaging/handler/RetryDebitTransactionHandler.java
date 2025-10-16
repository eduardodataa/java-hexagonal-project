package com.example.hexagonal.infrastructure.messaging.handler;

import com.example.hexagonal.domain.port.DebitTransactionService;
import com.example.hexagonal.infrastructure.messaging.dto.RetryDebitTransactionCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetryDebitTransactionHandler extends CommandHandler<RetryDebitTransactionCommand> {
    
    private final DebitTransactionService debitTransactionService;
    
    @Override
    public String getCommandType() {
        return "RETRY_DEBIT_TRANSACTION";
    }
    
    @Override
    public Class<RetryDebitTransactionCommand> getCommandClass() {
        return RetryDebitTransactionCommand.class;
    }
    
    @Override
    public void handle(RetryDebitTransactionCommand command) {
        log.info("Processing retry debit transaction command for transaction: {}", command.getTransactionId());
        
        debitTransactionService.retryFailedTransaction(command.getTransactionId());
        
        log.info("Debit transaction retry initiated for transaction: {}", command.getTransactionId());
    }
}
