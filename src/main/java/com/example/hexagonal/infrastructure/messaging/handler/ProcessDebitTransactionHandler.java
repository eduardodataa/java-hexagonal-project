package com.example.hexagonal.infrastructure.messaging.handler;

import com.example.hexagonal.domain.port.DebitTransactionService;
import com.example.hexagonal.infrastructure.messaging.dto.ProcessDebitTransactionCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessDebitTransactionHandler extends CommandHandler<ProcessDebitTransactionCommand> {
    
    private final DebitTransactionService debitTransactionService;
    
    @Override
    public String getCommandType() {
        return "PROCESS_DEBIT_TRANSACTION";
    }
    
    @Override
    public Class<ProcessDebitTransactionCommand> getCommandClass() {
        return ProcessDebitTransactionCommand.class;
    }
    
    @Override
    public void handle(ProcessDebitTransactionCommand command) {
        log.info("Processing debit transaction command for transaction: {}", command.getTransactionId());
        
        debitTransactionService.processDebitTransaction(command.getTransactionId());
        
        log.info("Debit transaction processing started for transaction: {}", command.getTransactionId());
    }
}
