package com.caiocaminha.expensesmanager.core.application.gateway.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class TestKafkaProducer {

    private KafkaTemplate<String, String> kafkaTemplate;


    public TestKafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessages() {
        var message = new Message() {
            @Override
            public Object getPayload() {
                return "testing";
            }

            @Override
            public MessageHeaders getHeaders() {
                return new MessageHeaders(
                        Map.of(
                                KafkaHeaders.KEY, "correlationKey",
                                KafkaHeaders.TOPIC, "topic1",
                                KafkaHeaders.KEY, "test"
                        )
                );
            }
        };

        log.info("sending message to topic1");
        kafkaTemplate.send("topic1", "test", "testing message publishing");

        log.info("sending message via Message abstraction");
        kafkaTemplate.send(message).whenComplete(
                (result, ex) -> {
                    log.info("sent successfully {}", result);
                }
        );
    }



}
