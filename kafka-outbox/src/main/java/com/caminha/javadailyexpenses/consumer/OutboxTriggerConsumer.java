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
    private final ObjectMapper objectMapper;
    private final OutboxPersistenceProvider outboxPersistenceProvider;

    @Value("${kafka.outbox.internal.topic}")
    public  String outboxInternalTopic;

    public OutboxTriggerConsumer(
            KafkaPublisher kafkaPublisher,
            ObjectMapper objectMapper,
            OutboxPersistenceProvider outboxPersistenceProvider
    ) {
        this.kafkaPublisher = kafkaPublisher;
        this.objectMapper = objectMapper;
        this.outboxPersistenceProvider = outboxPersistenceProvider;
    }

    @KafkaListener(topics = "${outbox.trigger.topic}")
    public void triggerOutbox() throws JsonProcessingException {

        //todo caio - this event is just to trigger the logic that processes outbox logic
        // retrieve all orderingKeys that have unpublished messages
        // trigger internal event only containing orderingKey
        // another consumer must consume this internal event
        // retrieve unpublished messages for that orderingKey and publish them to the broker
        // this way multiple pods, or internal threads / virtual threads can publish events for different
        // orderingKeys, allowing parallel consumption.

        outboxPersistenceProvider.findOrderingKeysForUnpublishedMessages().doOnNext( orderingKey ->
                kafkaPublisher.publishMessage(
                        orderingKey,
                        orderingKey,
                        outboxInternalTopic
                )
        );



    }

}
