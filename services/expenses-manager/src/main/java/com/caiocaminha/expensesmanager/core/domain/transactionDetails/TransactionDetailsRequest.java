package com.caiocaminha.expensesmanager.core.domain.transactionDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties
public record TransactionDetailsRequest(
        UUID id,
        UUID userId,
        Category category,
        String details,
        Double cost,
        String transactionDate,
        String paidBy
) {

    public TransactionDetails toDomain() {
        return new TransactionDetails(
                userId, category, details, cost, LocalDate.parse(transactionDate), paidBy
        );
    }

}
