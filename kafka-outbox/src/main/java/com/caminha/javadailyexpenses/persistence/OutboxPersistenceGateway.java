package com.caminha.javadailyexpenses.persistence;

import com.caminha.javadailyexpenses.config.OutboxConfigProperties;
import com.caminha.javadailyexpenses.consumer.OutboxEvent;
import com.caminha.kafkautils.publisher.OutboxBaseClass;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Repository
@Slf4j
public class OutboxPersistenceGateway implements OutboxPersistenceProvider {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final OutboxConfigProperties outboxConfigProperties;

    public OutboxPersistenceGateway(
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper,
            OutboxConfigProperties outboxConfigProperties
    ) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.outboxConfigProperties = outboxConfigProperties;
    }

    @Override
    public Flux<String> findOrderingKeysForUnpublishedMessages() {
        log.info("Finding ordering keys for unpublished messages");
        return outboxRepository.findOrderingKeysForUnpublishedMessages();
    }

    @Override
    public Flux<OutboxEvent> findUnpublishedMessagesByOrderingKey(String orderingKey) {
        return outboxRepository.findByOrderingKeyAndSentAtIsNull(orderingKey).map(OutboxEntity::toDomain);
    }

    @Override
    public Mono<Void> markAsSent(String id) {
        return outboxRepository.markAsSent(id);
    }

    @Override
    public <T extends OutboxBaseClass> Mono<OutboxEvent> save(T payload, OrderingKeyExtractor<T> orderingKeyExtractor) throws JsonProcessingException {
        String jsonPayload = objectMapper.writeValueAsString(payload);
        String eventType = objectMapper.readTree(jsonPayload).path("type").asText();

        log.info("Saving outbox message of type: {}", eventType);

        return outboxRepository.save(
                OutboxEntity.from(
                        jsonPayload,
                        outboxConfigProperties.findTopicNameByType(eventType),
                        orderingKeyExtractor.extract(payload)
                )
        ).map(OutboxEntity::toDomain);
    }


}
