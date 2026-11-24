package com.caiocaminha.expensesmanager.core.domain.billing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


//todo Do I need to keep record of Billing and Alerts if they will be managed on kt-analytics-service?
// maybe the history could be the outbox table

public record BillingDetails(
        UUID id,
        UUID userId,
        UUID alertId,
        String description,
        BigDecimal cost,
        LocalDate expirationDate
) {
}
