package com.caminha.javadailyexpenses;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

public class ReactiveRedisTemplateUtils {

    public static <K, V> ReactiveRedisTemplate<SimpleRedisKey<K>, V> createReactiveRedisTemplate(
            Class<K> keyClass,
            Class<V> valueClass,
            ReactiveRedisConnectionFactory connectionFactory
    ) {
        ObjectMapper objectMapper = ObjectMapperUtils.defaultObjectMapper();

        Jackson2JsonRedisSerializer<V> valueSerializer = new Jackson2JsonRedisSerializer<>(
                objectMapper,
                objectMapper.getTypeFactory().constructArrayType(valueClass)
        );

        RedisSerializationContext.RedisSerializationContextBuilder<SimpleRedisKey<K>, V> context =
                RedisSerializationContext.newSerializationContext(
                        new Jackson2JsonRedisSerializer<SimpleRedisKey<K>>(
                                objectMapper,
                                objectMapper.getTypeFactory().constructParametricType(SimpleRedisKey.class, keyClass)
                        )
                );

        return new ReactiveRedisTemplate<>(connectionFactory, context
                .value(valueSerializer)
                .hashValue(valueSerializer)
                .build());
    }


}
