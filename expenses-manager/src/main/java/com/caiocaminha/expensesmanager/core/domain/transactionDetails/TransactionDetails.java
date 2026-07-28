package com.caiocaminha.expensesmanager.core.domain.transactionDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record TransactionDetails(
        UUID id,
        UUID userId,
        Category category,
        String details,
        Double cost,
        LocalDate transactionDate,
        String paidBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {


    public TransactionDetails(
            UUID userId,
            Category category,
            String details,
            Double cost,
            LocalDate transactionDate,
            String paidBy
    ) {
        this(
                UUID.randomUUID(),
                userId,
                category,
                details,
                cost,
                transactionDate,
                paidBy,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public int internalHashCode() {
        return Objects.hash(this.details, this.transactionDate, this.cost, this.userId);
    }

}
