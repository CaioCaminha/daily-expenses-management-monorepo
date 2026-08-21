package com.caminha.kafkautils.config;

import java.util.Objects;

public record KafkaTopicDefinition(
        String name,
        Integer partitions,
        Integer replicas
) {

    @Override
    public Integer partitions() {
        return Objects.requireNonNullElse(partitions, 1);
    }

    @Override
    public Integer replicas() {
        return Objects.requireNonNullElse(replicas, 1);
    }
}
