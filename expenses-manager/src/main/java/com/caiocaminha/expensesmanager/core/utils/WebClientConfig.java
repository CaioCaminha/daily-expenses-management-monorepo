package com.caiocaminha.expensesmanager.core.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Value("${client.https.proxy.host}")
    public String proxyHost;
    @Value("${client.https.proxy.port}")
    public String proxyPort;
    @Value("${client.reactor.netty.pool.maxConnections}")
    public int maxConnections;
    @Value("${client.reactor.netty.pool.acquireTimeout}")
    public long acquireTimeout;
    @Value("${client.https.ssl.handshakeTimeout}")
    public Duration handShakeTimeout = Duration.ofSeconds(30);
}
