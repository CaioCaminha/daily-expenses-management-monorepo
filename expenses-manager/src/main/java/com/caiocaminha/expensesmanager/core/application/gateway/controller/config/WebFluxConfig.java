package com.caiocaminha.expensesmanager.core.application.gateway.controller.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        //512 KB
        configurer.defaultCodecs().maxInMemorySize(512 * 1024);
    }


}
