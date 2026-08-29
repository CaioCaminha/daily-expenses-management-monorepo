package com.caminha.javadailyexpenses;

public record  SimpleRedisKey<K>(
        String cacheName,
        K key
) {
}
