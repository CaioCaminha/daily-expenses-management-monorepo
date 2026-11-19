package com.caminha.javadailyexpenses.persistence;

import com.caminha.javadailyexpenses.consumer.OutboxEvent;
import com.caminha.kafkautils.publisher.OutboxBaseClass;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OutboxPersistenceProvider {

    Flux<String> findOrderingKeysForUnpublishedMessages();

    Flux<OutboxEvent> findUnpublishedMessagesByOrderingKey(String orderingKey);

    Mono<Void> markAsSent(String id);

    <T extends OutboxBaseClass> Mono<OutboxEvent> save(
            T payload,
            OrderingKeyExtractor<T> orderingKeyExtractor
    ) throws JsonProcessingException;

}
