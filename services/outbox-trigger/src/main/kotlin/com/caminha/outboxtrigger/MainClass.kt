package com.caminha.outboxtrigger

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.errors.TopicExistsException
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Instant
import java.util.Properties
import kotlin.time.Duration.Companion.milliseconds

fun main() {

    val kafkaHost = System.getenv("KAFKA_BOOTSTRAP_SERVER").also {
        println("Bootstrap server is $it")
    }
    val outboxTopic = System.getenv("KAFKA_OUTBOX_TOPIC").also {
        println("Outbox topic is $it")
    }

    createTopicIfAbsent(
        bootstrapServer = kafkaHost,
        topicName = outboxTopic
    )

    val properties = Properties()

    properties["bootstrap.servers"] = kafkaHost
    properties["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
    properties["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
    properties["request.timeout.ms"] = "5000"
    properties["delivery.timeout.ms"] = "10000"  // must be >= linger.ms + request.timeout.ms
    properties["linger.ms"] = "0"

    val kafkaProducer = KafkaProducer(properties, StringSerializer(), StringSerializer())

    while (true) {
        runBlocking {
            runCatching {
                outboxTriggerRunner(
                    kafkaProducer = kafkaProducer,
                    outboxTopic = outboxTopic,
                )
            }.onFailure {
                println("Restarting outbox-trigger runner | failure: ${it.message}")
            }
        }
    }

}

fun createTopicIfAbsent(
    bootstrapServer: String,
    topicName: String,
    partitions: Int = 1,
    replicationFactor: Short = 1,
) {
    val properties = Properties().apply {
        put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
        put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000)
    }
    println("AdminClient bootstrap servers resolved to: '${properties[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG]}'")
    AdminClient.create(properties).use { adminClient ->
        val outboxTriggerTopic = NewTopic(topicName, partitions, replicationFactor)

        runCatching {
            adminClient.createTopics(listOf(outboxTriggerTopic))
        }.onFailure {
            if(it.cause is TopicExistsException) {
                println("Topic already exists | skipping creation")
            } else {
              throw it
            }
        }

    }
}

private suspend fun outboxTriggerRunner(
    kafkaProducer: KafkaProducer<String, String>,
    outboxTopic: String
) {
    while (true) {
        kafkaProducer.flush()
        kafkaProducer.send(
            ProducerRecord(outboxTopic, "placeHolder", "placeHolder")
        ) { metadata, exception: Exception? ->
            exception?.let {
                println("Failed to trigger outbox-trigger: ${exception.message}")
            } ?: println("Triggered outbox-trigger at ${Instant.now()}")
        }

        delay(5000.milliseconds) // intended to trigger outbox every 5 seconds
    }
}