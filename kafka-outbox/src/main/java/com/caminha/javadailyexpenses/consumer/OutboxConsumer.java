package com.caminha.javadailyexpenses.consumer;

import com.caminha.javadailyexpenses.persistence.OutboxPersistenceProvider;
import com.caminha.kafkautils.publisher.KafkaPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


/**
 * This consumer is triggered by OutboxTriggerConsumer, receives one orderingKey containing unpublished messages
 */
@Service
@Slf4j
public class OutboxConsumer {

    private final OutboxPersistenceProvider outboxPersistenceProvider;

    private final KafkaPublisher kafkaPublisher;

    public OutboxConsumer(
        OutboxPersistenceProvider outboxPersistenceProvider,
        KafkaPublisher kafkaPublisher
    ) {
        this.outboxPersistenceProvider = outboxPersistenceProvider;
        this.kafkaPublisher = kafkaPublisher;
    }

    //todo caio - implement redis integration
    // Need concurrency control over who is processing outbox logic for a specific ordering_key
    // this is to prevent more than one pod or more than one thread within a pod processing the same ordering_key
    // pay attention to possible toc-tou race condition - redis query must handle this

@KafkaListener(topics = "${outbox.consumer.topic}", concurrency = "${outbox.consumer.concurrency:3}")
    public void outboxConsumer(String orderingKey) {
        outboxPersistenceProvider.findUnpublishedMessagesByOrderingKey(
                orderingKey
        ).flatMap(outboxEvent ->
                kafkaPublisher.publishMessage(
                        outboxEvent.payload(),
                        outboxEvent.orderingKey(),
                        outboxEvent.topicName()
                ).then(outboxPersistenceProvider.markAsSent(outboxEvent.id()))
        );

    }

}
