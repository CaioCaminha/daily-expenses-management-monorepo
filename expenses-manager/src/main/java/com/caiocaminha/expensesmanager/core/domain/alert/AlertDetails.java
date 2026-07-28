package com.caiocaminha.expensesmanager.core.domain.alert;

import java.time.LocalDate;
import java.util.UUID;

public record AlertDetails(
        UUID id,
        UUID userId,
        AlertType type,
        LocalDate dueDate
){}


