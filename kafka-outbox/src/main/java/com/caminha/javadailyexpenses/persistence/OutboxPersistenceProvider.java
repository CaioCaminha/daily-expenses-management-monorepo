package com.caminha.javadailyexpenses.persistence;

import com.caminha.javadailyexpenses.consumer.OutboxEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OutboxPersistenceProvider {

    Flux<String> findOrderingKeysForUnpublishedMessages();

    Flux<OutboxEvent> findUnpublishedMessagesByOrderingKey(String orderingKey);

    Mono<Void> markAsSent(String id);

    //todo save method should receive an object as the message payload, orderId provider, topicName
    // could resolve topicName by type based on properties
    Mono<Void> save()

}
