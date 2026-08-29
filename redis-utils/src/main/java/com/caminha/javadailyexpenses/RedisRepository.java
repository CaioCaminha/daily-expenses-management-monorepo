package com.caminha.javadailyexpenses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RedisRepository <K, V> {

    private final static String UNLOCK_SCRIPT = """
        if redis.call('GET', KEYS[1]) == ARGV[1] then
            return redis.call('DEL', KEYS[1])
        else
            return 0
        end
        """;

    private ReactiveRedisTemplate<SimpleRedisKey<K>, V> redisTemplate;
    private final String cacheName;

    public RedisRepository(
            ReactiveRedisTemplate<SimpleRedisKey<K>, V> redisTemplate,
            String cacheName
    ) {
        this.redisTemplate = redisTemplate;
        this.cacheName = cacheName;
    }

    public Mono<V> findById(
            K key,
            Class<V> valueType
    ) {
        log.info("Finding value for key: {}", key);
        return redisTemplate.opsForValue().get(buildSimpleRedisKey(key));
    }

    public Mono<Boolean> saveIfAbsent(
            K key,
            V value
    ) {
        return redisTemplate.opsForValue()
                .setIfAbsent(buildSimpleRedisKey(key), value, Duration.ofDays(1));
    }

    public Mono<Boolean> safeDelete(
            K key
    ){
        return redisTemplate.execute(
                RedisScript.of(UNLOCK_SCRIPT, Long.class),
                List.of(buildSimpleRedisKey(key))
        ).count().map(count -> count > 0).doOnError(error ->
                log.error("Not possible to safe delete for key: {}", key, error)
        );
    }





    private SimpleRedisKey<K> buildSimpleRedisKey(
            K key
    ) {
        return new SimpleRedisKey<>(
                cacheName, key
        );
    }



}
