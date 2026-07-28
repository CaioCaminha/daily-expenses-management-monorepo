package com.caminha.kafkautils.outbox.entities;


import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;

public class OutboxEntity implements Persistable<String> {
    @Override
    public @Nullable String getId() {
        return "";
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
