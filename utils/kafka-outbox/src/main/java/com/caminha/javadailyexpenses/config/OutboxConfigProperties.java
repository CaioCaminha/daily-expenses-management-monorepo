package com.caminha.javadailyexpenses.config;

import com.caminha.javadailyexpenses.persistence.TopicDetails;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ConfigurationProperties(prefix = "outbox.config")
public record OutboxConfigProperties(Map<String, TopicDetails> topics) {

    public String findTopicNameByType(String eventType) {
        Optional<Map.Entry<String, TopicDetails>> optionalEntryStream =  topics.entrySet().stream()
                .filter(entry -> entry.getValue().supportedTypes().contains(eventType))
                .findFirst();

        if(optionalEntryStream.isPresent()) {
            return optionalEntryStream.get().getKey();
        } else {
            throw new RuntimeException("Topic not found for event type: " + eventType);
        }
    }

}
