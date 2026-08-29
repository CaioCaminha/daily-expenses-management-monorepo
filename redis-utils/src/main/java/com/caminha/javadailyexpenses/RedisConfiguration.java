package com.caminha.javadailyexpenses;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Value("{redis.config.host_name}")
    public String redisHost;

    @Value("{redis.config.port}")
    public Integer redisPort;

    @Value("{redis.config.password}")
    public String redisPassword;


    @Bean
    // TODO future - Evaluate SSL LettuceConnectionFactory for redis connections - depending on deployment
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);
        configuration.setPassword(RedisPassword.of(redisPassword));

        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);

        factory.setEagerInitialization(true);

        return factory;
    }


}
