package com.caminha.kafkautils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@NoArgsConstructor
public class KafkaConsumerTest {

    @KafkaListener(topics = "outbox-test")
    public void consumer(String payload) {
        log.info("Message consumed: {}", payload);
    }
}
