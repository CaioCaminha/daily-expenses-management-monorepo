package com.caminha.javadailyexpenses;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConfiguration;

@ComponentScan
@Configuration
@Import(RedisConfiguration.class)
public class KafkaOutboxModuleConfiguration {

    @Bean
    public RedisRepository<String, String> outboxOrderingKeyRedisRepository(
            ReactiveRedisConnectionFactory connectionFactory
    ) {
        return new RedisRepository<>(
                ReactiveRedisTemplateUtils.createReactiveRedisTemplate(
                        String.class,
                        String.class,
                        connectionFactory
                ),
                "outbox-ordering-key-lock"
        );
    }

}
