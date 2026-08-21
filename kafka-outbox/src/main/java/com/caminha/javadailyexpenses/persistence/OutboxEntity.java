package com.caminha.javadailyexpenses.persistence;


import com.caminha.javadailyexpenses.consumer.OutboxEvent;
import com.caminha.postgresutils.utils.utils.persistence.PersistableEntity;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table(name = OutboxEntity.TABLE_NAME)
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
}
