package com.caminha.javadailyexpenses.persistence;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface OutboxRepository extends ReactiveCrudRepository<OutboxEntity, UUID> {

    @Query("""
            SELECT DISTINCT ordering_key from outbox where sent_at is null;
            """)
    Flux<String> findOrderingKeysForUnpublishedMessages();

    @Query("""
            SELECT * FROM outbox where sent_at is null and ordering_key = :orderingKey;
            """)
    Flux<OutboxEntity> findByOrderingKeyAndSentAtIsNull(String orderingKey);

    @Query("""
            UPDATE outbox SET sent_at = NOW() where id = :id;
            """)
    Mono<Void> markAsSent(String id);

}
