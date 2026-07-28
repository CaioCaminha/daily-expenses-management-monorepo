package com.caiocaminha.expensesmanager.core.application.gateway.api.openai;

import com.caiocaminha.expensesmanager.core.domain.transactionDetails.TransactionDetails;

import java.util.Set;

public class OpenAIGateway {
    private final OpenAiHttpClient httpClient;
    private final OpenAiClientProperties properties;

    public OpenAIGateway(
            OpenAiHttpClient httpClient,
            OpenAiClientProperties properties
    ) {
        this.httpClient = httpClient;
        this.properties = properties;
    }

    public Set<TransactionDetails> calculateCategories(Set<TransactionDetails> statements) {
        //todo pending implementation
        return null;
    }
}
