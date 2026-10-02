package com.caminha.javadailyexpenses;

import com.caminha.javadailyexpenses.config.OutboxConfigProperties;
import com.caminha.postgresutils.utils.config.R2DBCConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;

@ComponentScan
@Configuration
@EnableR2dbcRepositories
@Import({
        RedisConfiguration.class,
        R2DBCConfiguration.class
})
@EnableConfigurationProperties({OutboxConfigProperties.class})
public class KafkaOutboxModuleConfiguration {

    @Bean
    public RedisRepository<String, String> outboxOrderingKeyRedisRepository(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory
    ) {
        return new RedisRepository<>(
                ReactiveRedisTemplateUtils.createReactiveRedisTemplate(
                        String.class,
                        String.class,
                        reactiveRedisConnectionFactory
                ),
                "outbox-ordering-key-lock"
        );
    }

}
