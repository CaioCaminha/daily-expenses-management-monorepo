package com.caminha.kafkautils.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

@Service
public class KafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public KafkaPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     *
     * @param orderingKey Provides key, used by kafka producer to designate to which partition the message will be sent
     */
    public <T> Mono<SendResult<String, String>> publishMessage(
            T payload,
            String orderingKey,
            String topic
    ){
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(payload)).flatMap( json ->
                Mono.fromFuture(() -> kafkaTemplate.send(topic, orderingKey, json))
        );
    }


}
