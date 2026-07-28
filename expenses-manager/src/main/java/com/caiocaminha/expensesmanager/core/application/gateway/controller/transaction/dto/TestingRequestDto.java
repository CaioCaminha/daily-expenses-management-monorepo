package com.caiocaminha.expensesmanager.core.application.gateway.controller.transaction.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record TestingRequestDto(
        String id,
        String details,
        Timestamp date,
        BigDecimal amount
) {
}
