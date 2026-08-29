package com.caminha.javadailyexpenses.consumer;

import com.caminha.javadailyexpenses.persistence.OutboxPersistenceProvider;
import com.caminha.kafkautils.publisher.KafkaPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OutboxTriggerConsumer {

    private final KafkaPublisher kafkaPublisher;
    private final OutboxPersistenceProvider outboxPersistenceProvider;

    @Value("${kafka.outbox.internal.topic}")
    public  String outboxInternalTopic;

    public OutboxTriggerConsumer(
            KafkaPublisher kafkaPublisher,
            ObjectMapper objectMapper,
            OutboxPersistenceProvider outboxPersistenceProvider
    ) {
        this.kafkaPublisher = kafkaPublisher;
        this.outboxPersistenceProvider = outboxPersistenceProvider;
    }

    @KafkaListener(topics = "${outbox.trigger.topic}")
    public void triggerOutbox() throws JsonProcessingException {

        //todo caio - this needs to try to lock given ordering key before calling kafkaPublisher

        outboxPersistenceProvider.findOrderingKeysForUnpublishedMessages().doOnNext( orderingKey ->
                kafkaPublisher.publishMessage(
                        orderingKey,
                        orderingKey,
                        outboxInternalTopic
                )
        );



    }

}
