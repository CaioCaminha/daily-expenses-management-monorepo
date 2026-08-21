package com.caminha.javadailyexpenses.persistence;

import com.caminha.javadailyexpenses.consumer.OutboxEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OutboxPersistenceProvider {

    Flux<String> findOrderingKeysForUnpublishedMessages();

    Flux<OutboxEvent> findUnpublishedMessagesByOrderingKey(String orderingKey);

    Mono<Void> markAsSent(String id);

}
