package com.caiocaminha.expensesmanager.core.application.gateway.r2dbc;

import com.caminha.kafkautils.publisher.OutboxBaseClass;

public record OutboxTestDto(
        @Override String type,
        String data
) implements OutboxBaseClass {
}
