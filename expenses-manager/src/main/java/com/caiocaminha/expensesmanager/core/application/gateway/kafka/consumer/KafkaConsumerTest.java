package com.caiocaminha.expensesmanager.core.application.gateway.kafka.consumer;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@NoArgsConstructor
public class KafkaConsumerTest {

    @KafkaListener(topics = "topic1", groupId = "groupId")
    public void consumer(String payload) {
        log.info("Message consumed: {}", payload);
    }
}
