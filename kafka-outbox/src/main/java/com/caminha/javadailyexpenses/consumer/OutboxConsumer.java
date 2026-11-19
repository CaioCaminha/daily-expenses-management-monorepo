package com.caminha.javadailyexpenses.consumer;

import com.caminha.javadailyexpenses.RedisRepository;
import com.caminha.javadailyexpenses.persistence.OutboxPersistenceProvider;
import com.caminha.kafkautils.publisher.KafkaPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/**
 * This consumer is triggered by OutboxTriggerConsumer, receives one orderingKey containing unpublished messages
 */
@Service
@Slf4j
public class OutboxConsumer {

    private final OutboxPersistenceProvider outboxPersistenceProvider;
    private final RedisRepository<String, String> redisRepository;
    private final KafkaPublisher kafkaPublisher;

    public OutboxConsumer(
        OutboxPersistenceProvider outboxPersistenceProvider,
        RedisRepository<String, String> redisRepository,
        KafkaPublisher kafkaPublisher
    ) {
        this.outboxPersistenceProvider = outboxPersistenceProvider;
        this.redisRepository = redisRepository;
        this.kafkaPublisher = kafkaPublisher;
    }

    //TODO - Future Improvement - Handle deduplication of events - storing a hash of the payload, orderingKey and topic
    @KafkaListener(topics = "${outbox.consumer.topic}", concurrency = "${outbox.consumer.concurrency:3}")
    public void outboxConsumer(String orderingKey) {
        publishUnpublishedMessages(orderingKey).subscribe();
    }

    private Mono<Void> publishUnpublishedMessages(String orderingKey) {
       return outboxPersistenceProvider.findUnpublishedMessagesByOrderingKey(orderingKey)
                .concatMap(outboxEvent -> {
                    log.info("Found unpublished event {}", outboxEvent.id());
                    return kafkaPublisher.publishMessage(
                            outboxEvent.payload(),
                            outboxEvent.orderingKey(),
                            outboxEvent.topicName()
                    ).flatMap(result -> {
                        log.info("Successfully published event {} with orderingKey {}",
                                outboxEvent.id(), outboxEvent.orderingKey());
                        return outboxPersistenceProvider.markAsSent(outboxEvent.id())
                                .then(Mono.defer(() -> {
                                    log.info("Unlocking orderingKey {}", outboxEvent.orderingKey());
                                    return redisRepository.safeDelete(outboxEvent.orderingKey());
                                }));
                    }).doOnError(error -> log.error("Error occurred while publishing event {}", outboxEvent.id(), error));
                }).then();
    }

}
