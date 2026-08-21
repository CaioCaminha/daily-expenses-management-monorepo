package com.caminha.kafkautils;

//import com.caiocaminha.expensesmanager.core.application.gateway.kafka.consumer.KafkaConsumerTest;
import com.caminha.javadailyexpenses.JsonUtilsConfiguration;
import com.caminha.kafkautils.config.KafkaTopicsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@EnableKafka
@Configuration
@EnableConfigurationProperties(
        {
                KafkaTopicsProperties.class,
                JsonUtilsConfiguration.class
        }
)
@ComponentScan
public class KafkaConfig {

    private final KafkaTopicsProperties kafkaTopicsProperties;

    public KafkaConfig(
            KafkaTopicsProperties kafkaTopicsProperties
    ) {
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    @Bean
    public Set<NewTopic> kafkaTopics() {
        return this.kafkaTopicsProperties.getAllTopics().stream().map(topic ->
                TopicBuilder
                        .name(topic.name())
                        .partitions(topic.partitions())
                        .replicas(topic.replicas())
                        .build()
                ).collect(Collectors.toSet());
    }


    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "groupId");
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        var producer = new DefaultKafkaProducerFactory<String, String>(configProps);
        //creates a producer per thread instead of sharing a singleton among all threads
        producer.setProducerPerThread(true);
        return producer;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new KafkaAdmin(configs);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        //enables micrometer observation | Timers are managed with each observation
        factory.getContainerProperties().setObservationEnabled(true);

        factory.setConcurrency(Runtime.getRuntime().availableProcessors());
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }


//    @Bean
//    public KafkaConsumerTest consumer() {
//        return new KafkaConsumerTest();
//    }


    @EventListener(ContextClosedEvent.class)
    public void producerFactoryCleanup() {
        producerFactory().closeThreadBoundProducer();
    }



}
