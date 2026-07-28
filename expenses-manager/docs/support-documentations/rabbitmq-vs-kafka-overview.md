# RabbitMQ vs Kafka evaluation overview

To this moment Kafka will be chosen over RabbitMQ
Despite the fact that RabbitMQ fits our requirements of a simple messaging system.

Kafka automatically allows retention of messages and replaying of events, which is fundamental for the ledger pattern that will later be implemented.

Kafka also implements several performance improvements like pagecaching relying on the filesystem as persistence layer.
Using Unix sendfile reducing byte copying.
And implements batching on the disk level, batching network packets, resulting in larger sequential disk operations
at the producer layer as well, kafka producer will attempt to accumulate data in memory and to send out larger batches in a single request

Needs to create an abstraction for publishing messages into kafka, and wrapp KafkaTemplate CompletableFuture under a Publisher (Mono)