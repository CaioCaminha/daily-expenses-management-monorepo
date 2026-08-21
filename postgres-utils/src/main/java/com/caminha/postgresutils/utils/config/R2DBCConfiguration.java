package com.caminha.postgresutils.utils.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
/**
 * Validate that this is enough for transaction management configuration
 */
@EnableR2dbcRepositories
@EnableConfigurationProperties({R2DBCProperties.class})
@ComponentScan
public class R2DBCConfiguration extends AbstractR2dbcConfiguration {

    private final R2DBCProperties properties;

    public R2DBCConfiguration(
            R2DBCProperties properties
    ) {
        this.properties = properties;
    }

    @Bean
    public R2dbcTransactionManager r2dbcTransactionManager(
            ConnectionFactory connectionFactory
    ) {
        return new R2dbcTransactionManager(connectionFactory);
    }


    @Override
    @Bean
    public ConnectionPool connectionFactory() {
        log.info("Initializing ConnectionFactory with Connection Pool");
        Map<String, String> options = new HashMap<>();
        options.put("lock_timeout", "10s");
        options.put("transaction_timeout", "40s"); // todo review if its a good timeout 20s
        options.put("idle_in_transaction_session_timeout", "120s");
        options.put("tcp_keepalives_idle", "300s");
        options.put("tcp_keepalives_interval", "5s");
        options.put("client_connection_check_interval", "120s");

        ConnectionFactory connectionFactory =  new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration
                        .builder()
                        .username(properties.user)
                        .password(properties.password)
                        .database(properties.name)
                        .host(properties.server.host)
                        .port(properties.server.port)
                        .options(options)
                        //adding AlertType to EnumMap
                        //driver also registers an array variant of the codec alert_type[]
//                        .codecRegistrar(
//                                EnumCodec.builder()
//                                        .withEnum("alert_type", AlertType.class)
//                                        .build()
//                        )
                        .build()
        );

        // from r2dbc-pool wraps connectionFactory on a ConnectionPool, which manages connection pooling
        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder()
                .connectionFactory(connectionFactory)
                .initialSize(16)
                .maxSize(32)
                .maxIdleTime(Duration.ofDays(1))
                .maxLifeTime(Duration.ofDays(3))
                .maxAcquireTime(Duration.ofSeconds(10))
                .build();

        return new ConnectionPool(poolConfiguration);
    }

}

