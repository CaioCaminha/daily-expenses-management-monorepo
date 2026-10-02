package com.caminha.javadailyexpenses.consumer;

import com.caminha.javadailyexpenses.RedisRepository;
import com.caminha.javadailyexpenses.persistence.OutboxPersistenceProvider;
import com.caminha.kafkautils.publisher.KafkaPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
public class OutboxTriggerConsumer {

    private final KafkaPublisher kafkaPublisher;
    private final OutboxPersistenceProvider outboxPersistenceProvider;
    private final RedisRepository<String, String> redisRepository;

    @Value("${outbox.consumer.topic}")
    public  String outboxInternalTopic;

    public OutboxTriggerConsumer(
            KafkaPublisher kafkaPublisher,
            OutboxPersistenceProvider outboxPersistenceProvider,
            RedisRepository<String, String> redisRepository
    ) {
        this.kafkaPublisher = kafkaPublisher;
        this.outboxPersistenceProvider = outboxPersistenceProvider;
        this.redisRepository = redisRepository;
    }

    @KafkaListener(topics = "${outbox.trigger.topic}")
    public void triggerOutbox() throws JsonProcessingException {
        log.info("outbox triggered");
        outboxPersistenceProvider.findOrderingKeysForUnpublishedMessages()
                        .concatMap(orderingKey -> redisRepository.saveIfAbsent(orderingKey, orderingKey)
                                .flatMap(saveResult -> {
                                    if(saveResult == true) {
                                        return kafkaPublisher.publishMessage(
                                                orderingKey,
                                                orderingKey,
                                                outboxInternalTopic
                                        );
                                    } else {
                                        return Mono.empty();
                                    }
                                })
                                .doOnError(error -> log.error("Failed to process ordering key: {}", orderingKey))
                                .onErrorResume(error -> Mono.empty())
                        ).then().block();

    }

}
