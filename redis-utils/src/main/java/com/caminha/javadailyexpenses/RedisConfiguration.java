package com.caminha.javadailyexpenses;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@ComponentScan
@Slf4j
public class RedisConfiguration {

    @Value("${redis.host}")
    public String redisHost;

    @Value("${redis.port}")
    public String redisPort;

    @Value("${redis.password}")
    public String redisPassword;


    @Bean
    @Primary
    // TODO future - Evaluate SSL LettuceConnectionFactory for redis connections - depending on deployment
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        log.info("redis port: {}", redisPort);
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(Integer.parseInt(redisPort));
        configuration.setPassword(RedisPassword.of(redisPassword));

        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);

        factory.setEagerInitialization(true);

        return factory;
    }


}
