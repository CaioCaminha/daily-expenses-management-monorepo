package com.caminha.javadailyexpenses.persistence;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "outbox.config")
public record OutboxConfigProperties(Map<String, String> topics) {

    public String findTopicNameByType(String eventType) {
        return topics.get(eventType);
    }

}
