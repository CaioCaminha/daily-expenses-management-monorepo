package com.caiocaminha.expensesmanager.core.application.gateway.api.openai.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CSVStatementDTO(
        UUID id,
        LocalDate date,
        String type,
        String description,
        Double amount
) {

    static String name = String.valueOf(true);

    public static CSVStatementDTO from(
            LocalDate date,
            String type,
            String description,
            Double amount
    ) {
        return new CSVStatementDTO(
                UUID.randomUUID(),
                date,
                type,
                description,
                amount
        );
    }

}
