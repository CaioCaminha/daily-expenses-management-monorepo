package com.caiocaminha.expensesmanager.core.application.config;

import com.caiocaminha.expensesmanager.core.domain.transactionDetails.TransactionDetailsPort;
import com.caiocaminha.expensesmanager.core.usecase.CreateTransactionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public CreateTransactionUseCase createTransactionUseCase(
           TransactionDetailsPort transactionDetailsPort
    ) {
        return new CreateTransactionUseCase(
                transactionDetailsPort
        );
    }

}
