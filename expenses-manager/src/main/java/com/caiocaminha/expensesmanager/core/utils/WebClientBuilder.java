package com.caiocaminha.expensesmanager.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.*;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


@Configuration
public class WebClientBuilder {

    private WebClient.Builder clientBuilder;

    private final int maxMemory = 12 * 1024;
    private final int byteSize = 1024;

    private Logger logger = LoggerFactory.getLogger(WebClientBuilder.class);


    public HttpClient httpClient(
            String name,
            WebClientConfig config
    ) {
        var httpClient = HttpClient.create(
                ConnectionProvider.builder("http-connection-pool-%s".formatted(name))
                        .maxConnections(config.maxConnections)
                        .pendingAcquireTimeout(Duration.ofSeconds(config.acquireTimeout))
                        .build()
        );
        if(!config.proxyHost.isBlank()){
            httpClient.proxy(
                    (proxy) -> {
                            proxy.type(ProxyProvider.Proxy.HTTP)
                                    .host(config.proxyHost)
                                    .port(Integer.parseInt(config.proxyPort));
                    }
            );
        }
        return httpClient.compress(true);
    }

    public WebClient build(
            String baseUrl,
            Optional<List<ExchangeFilterFunction>> filters,
            Optional<List<ExchangeFilterFunction>> changeUrlFilters,
            HttpClient httpClient,
            Consumer<ClientCodecConfigurer> codecConfigurer
    ) {
        var connector = new ReactorClientHttpConnector(httpClient);
        return clientBuilder.clone().baseUrl(baseUrl)
                .clientConnector(connector)
                .codecs(codec -> codec.defaultCodecs().maxInMemorySize(maxMemory * byteSize))
                .codecs(codecConfigurer)
                .filters(
                        (filter) -> {
                            changeUrlFilters.ifPresent(filter::addAll);
                            filters.ifPresent(filter::addAll);
                            filter.add(logResponseFilter());
                        }
                )
                .build();
    }

    private ExchangeFilterFunction logResponseFilter() {

        //todo Log Response - Not sure how to do this in Java
        return (request, next) -> {
            logger.info(request.headers().toString());

            return next.exchange(request).single();
        };
    }




}


