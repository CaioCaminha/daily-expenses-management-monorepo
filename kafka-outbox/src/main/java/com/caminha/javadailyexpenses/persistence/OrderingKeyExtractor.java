package com.caminha.javadailyexpenses.persistence;

@FunctionalInterface
public interface OrderingKeyExtractor<T> {
    String extract(T payload);
}
