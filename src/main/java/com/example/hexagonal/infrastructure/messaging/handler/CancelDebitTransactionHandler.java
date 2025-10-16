package com.example.hexagonal.infrastructure.messaging.handler;

import com.example.hexagonal.domain.port.DebitTransactionService;
import com.example.hexagonal.infrastructure.messaging.dto.CancelDebitTransactionCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDebitTransactionHandler extends CommandHandler<CancelDebitTransactionCommand> {
    
    private final DebitTransactionService debitTransactionService;
    
    @Override
    public String getCommandType() {
        return "CANCEL_DEBIT_TRANSACTION";
    }
    
    @Override
    public Class<CancelDebitTransactionCommand> getCommandClass() {
        return CancelDebitTransactionCommand.class;
    }
    
    @Override
    public void handle(CancelDebitTransactionCommand command) {
        log.info("Processing cancel debit transaction command for transaction: {}", command.getTransactionId());
        
        debitTransactionService.cancelTransaction(command.getTransactionId(), command.getReason());
        
        log.info("Debit transaction cancelled for transaction: {}", command.getTransactionId());
    }
}