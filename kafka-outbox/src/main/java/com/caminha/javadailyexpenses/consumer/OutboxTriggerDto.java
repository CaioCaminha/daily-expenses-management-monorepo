package com.caminha.javadailyexpenses.consumer;

public record OutboxTriggerDto(
        String orderKey
) {
}
