package com.caiocaminha.expensesmanager.core.application.gateway.api.openai;

import com.caiocaminha.expensesmanager.core.utils.WebClientBuilder;
import com.caiocaminha.expensesmanager.core.utils.WebClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OpenAiWebClientConfig {

    @Value("${client.openai.baseUrl}")
    private String openAiBaseUrl;

    public static final String OPENAI_WEBCLIENT_QUALIFIER = "OPENAI_WEBCLIENT_QUALIFIER";
    public static final String GOOGLE_SHEETS_WEBCLIENT_QUALIFIER = "GOOGLE_SHEETS_WEBCLIENT_QUALIFIER";



    private final WebClientBuilder webClientBuilder;

    private final WebClientConfig webClientConfig;

    public OpenAiWebClientConfig(
            WebClientConfig webClientConfig,
            WebClientBuilder webClientBuilder
    ) {
        this.webClientBuilder = webClientBuilder;
        this.webClientConfig = webClientConfig;
    }

    private HttpClient httpClient() {
        return webClientBuilder.httpClient(
                "openai-http-client", webClientConfig
        );
    }

    @Bean
    public WebClient openAiWebClient() {
        List<ExchangeFilterFunction> filters = new ArrayList<>();
        filters.add(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
           var attribute = (String) clientRequest.attribute("attributeName").get();
            return Mono.just(ClientRequest.from(clientRequest)
                    .header("asd", attribute)
                    .build());
        }));
        return webClientBuilder.build(
                openAiBaseUrl,
                Optional.of(filters),
                Optional.empty(),
                httpClient(),
                ClientCodecConfigurer::defaultCodecs
        );
    }





}
