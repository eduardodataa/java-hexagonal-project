package com.example.hexagonal.infrastructure.messaging.handler;

import com.example.hexagonal.domain.port.DebitTransactionService;
import com.example.hexagonal.infrastructure.messaging.dto.CreateDebitTransactionCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateDebitTransactionHandler extends CommandHandler<CreateDebitTransactionCommand> {
    
    private final DebitTransactionService debitTransactionService;
    
    @Override
    public String getCommandType() {
        return "CREATE_DEBIT_TRANSACTION";
    }
    
    @Override
    public Class<CreateDebitTransactionCommand> getCommandClass() {
        return CreateDebitTransactionCommand.class;
    }
    
    @Override
    public void handle(CreateDebitTransactionCommand command) {
        log.info("Processing create debit transaction command for company: {}", command.getCompanyId());
        
        debitTransactionService.createDebitTransaction(
            command.getCompanyId(),
            command.getCompanyDocument(),
            command.getCompanyName(),
            command.getBankAccountId(),
            command.getAmount(),
            command.getDescription(),
            command.getScheduledDate(),
            command.getCorrelationId()
        );
        
        log.info("Debit transaction created successfully for company: {}", command.getCompanyId());
    }
}
