package com.caminha.kafkautils.config;

import org.apache.kafka.shaded.com.google.protobuf.MapEntry;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ConfigurationProperties("kafka.topics")
public record KafkaTopicsProperties(Map<String, KafkaTopicDefinition> definitions) {


    public List<KafkaTopicDefinition> getAllTopics() {
        return new ArrayList<>(definitions.values());
    }

}
