package com.caminha.javadailyexpenses.persistence;


import com.caminha.javadailyexpenses.consumer.OutboxEvent;
import com.caminha.postgresutils.utils.utils.persistence.PersistableEntity;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = OutboxEntity.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEntity extends PersistableEntity<String> {
    @Id
    public String id;
    @Column("topic_name")
    public String topicName;
    @Column("payload")
    public String payload;
    @Column("sent_at")
    public LocalDateTime sentAt;
    @Column("is_duplicate")
    public Boolean isDuplicate;

    /**
     * orderingKey works for sharding, specific orderingKeys result on the same hash, therefore, the messages
     * with the same orderingKey are delivered to the same partition on kafka broker.
     */
    @Column("ordering_key")
    public String orderingKey;

    public OutboxEntity(
            String topicName,
            String payload,
            String orderingKey
    ) {
        this.id = UUID.randomUUID().toString();
        this.topicName = topicName;
        this.payload = payload;
        this.sentAt = null;
        this.isDuplicate = false;
        this.orderingKey = orderingKey;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public final static String TABLE_NAME = "outbox";

    @Override
    public @Nullable String getId() {
        return id;
    }

    public OutboxEvent toDomain() {
        return new OutboxEvent(
                this.id,
                this.topicName,
                this.payload,
                this.sentAt,
                this.isDuplicate,
                this.orderingKey,
                this.createdAt,
                this.updatedAt
        );
    }

    public static OutboxEntity from(
            String payload,
            String topicName,
            String orderingKey
    ) {
        return new OutboxEntity(
                topicName,
                payload,
                orderingKey
        );
    }
}
