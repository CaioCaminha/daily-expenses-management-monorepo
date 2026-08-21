package com.caminha.javadailyexpenses.consumer;

import java.time.LocalDateTime;

public record OutboxEvent(
        String id,
        String topicName,
        String payload,
        LocalDateTime sentAt,
        Boolean isDuplicate,
        String orderingKey,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
