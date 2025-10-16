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
public class ValidateDebitTransactionHandler extends CommandHandler<CreateDebitTransactionCommand> {
    
    private final DebitTransactionService debitTransactionService;
    
    @Override
    public String getCommandType() {
        return "VALIDATE_DEBIT_TRANSACTION";
    }
    
    @Override
    public Class<CreateDebitTransactionCommand> getCommandClass() {
        return CreateDebitTransactionCommand.class;
    }
    
    @Override
    public void handle(CreateDebitTransactionCommand command) {
        log.info("Validating debit transaction for company: {}", command.getCompanyId());
        
        if (command.getAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        if (command.getCompanyDocument() == null || command.getCompanyDocument().trim().isEmpty()) {
            throw new IllegalArgumentException("Company document is required");
        }
        
        log.info("Debit transaction validation passed for company: {}", command.getCompanyId());
    }
}
