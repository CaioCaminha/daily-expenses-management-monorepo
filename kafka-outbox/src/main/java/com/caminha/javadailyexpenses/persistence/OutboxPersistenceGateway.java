package com.caminha.javadailyexpenses.persistence;

import com.caminha.javadailyexpenses.consumer.OutboxEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class OutboxPersistenceGateway implements OutboxPersistenceProvider{

    private final OutboxRepository outboxRepository;

    public OutboxPersistenceGateway(
            OutboxRepository outboxRepository
    ) {
        this.outboxRepository = outboxRepository;
    }


    @Override
    public Flux<String> findOrderingKeysForUnpublishedMessages() {
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


}
